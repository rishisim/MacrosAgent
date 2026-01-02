package com.macros.agent.ui.screens.diary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.macros.agent.data.local.dao.DailyTotals
import com.macros.agent.data.local.entity.DiaryEntry
import com.macros.agent.data.local.entity.MealType
import com.macros.agent.data.local.entity.UserGoals
import com.macros.agent.data.repository.DiaryRepository
import com.macros.agent.data.repository.ExerciseRepository
import com.macros.agent.data.repository.GoalsRepository
import com.macros.agent.data.repository.UserMealRepository
import com.macros.agent.data.local.entity.UserMealItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

/**
 * UI state for the Diary screen.
 */
data class DiaryUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val entries: Map<MealType, List<DiaryEntry>> = emptyMap(),
    val dailyTotals: DailyTotals = DailyTotals(0f, 0f, 0f, 0f),
    val exerciseCalories: Int = 0,
    val goals: UserGoals = UserGoals(),
    val isLoading: Boolean = true
) {
    val remainingCalories: Int
        get() = goals.dailyCalories - dailyTotals.totalCalories.toInt() + exerciseCalories
    
    val calorieProgress: Float
        get() = if (goals.dailyCalories > 0) {
            (dailyTotals.totalCalories / goals.dailyCalories).coerceIn(0f, 1.5f)
        } else 0f
    
    val proteinProgress: Float
        get() = if (goals.proteinGrams > 0) {
            (dailyTotals.totalProtein / goals.proteinGrams).coerceIn(0f, 1.5f)
        } else 0f
    
    val carbsProgress: Float
        get() = if (goals.carbsGrams > 0) {
            (dailyTotals.totalCarbs / goals.carbsGrams).coerceIn(0f, 1.5f)
        } else 0f
    
    val fatProgress: Float
        get() = if (goals.fatGrams > 0) {
            (dailyTotals.totalFat / goals.fatGrams).coerceIn(0f, 1.5f)
        } else 0f
}

@HiltViewModel
class DiaryViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository,
    private val goalsRepository: GoalsRepository,
    private val exerciseRepository: ExerciseRepository,
    private val userMealRepository: UserMealRepository
) : ViewModel() {
    
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()
    
    init {
        syncGoogleFit()
    }

    private fun syncGoogleFit() {
        viewModelScope.launch {
            exerciseRepository.syncFromGoogleFit(_selectedDate.value)
        }
    }

    val diaryState: StateFlow<DiaryUiState> = _selectedDate
        .flatMapLatest { date ->
            combine(
                diaryRepository.getEntriesForDate(date),
                diaryRepository.getDailyTotals(date),
                exerciseRepository.getTotalCaloriesBurned(date),
                goalsRepository.getGoals()
            ) { entries, totals, exerciseCals, goals ->
                val groupedEntries = entries.groupBy { it.mealType }
                val orderedEntries = MealType.entries.associateWith { mealType ->
                    groupedEntries[mealType] ?: emptyList()
                }

                DiaryUiState(
                    selectedDate = date,
                    entries = orderedEntries,
                    dailyTotals = totals,
                    exerciseCalories = exerciseCals,
                    goals = goals ?: UserGoals(),
                    isLoading = false
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DiaryUiState(isLoading = true)
        )
    
    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
        syncGoogleFit()
    }
    
    fun goToPreviousDay() {
        _selectedDate.value = _selectedDate.value.minusDays(1)
        syncGoogleFit()
    }
    
    fun goToNextDay() {
        _selectedDate.value = _selectedDate.value.plusDays(1)
        syncGoogleFit()
    }
    
    fun goToToday() {
        _selectedDate.value = LocalDate.now()
    }
    
    fun deleteEntry(entry: DiaryEntry) {
        viewModelScope.launch {
            diaryRepository.deleteEntry(entry)
        }
    }
    
    fun duplicateEntry(entry: DiaryEntry) {
        viewModelScope.launch {
            val newEntry = entry.copy(
                id = 0,
                timestamp = System.currentTimeMillis()
            )
            diaryRepository.addEntry(newEntry)
        }
    }

    fun saveMealAsCustom(mealType: MealType, mealName: String) {
        val currentEntries = diaryState.value.entries[mealType] ?: return
        if (currentEntries.isEmpty()) return

        viewModelScope.launch {
            val items = currentEntries.map { entry ->
                UserMealItem(
                    mealId = 0,
                    foodName = entry.foodName,
                    servingSize = entry.servingSize,
                    servingUnit = entry.servingUnit,
                    servingsConsumed = entry.servingsConsumed,
                    calories = entry.calories,
                    protein = entry.protein,
                    carbs = entry.carbs,
                    fat = entry.fat,
                    fdcId = entry.fdcId
                )
            }
            userMealRepository.saveMeal(mealName, items)
        }
    }
}
