package com.macros.agent.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.macros.agent.data.local.entity.UserGoals
import com.macros.agent.data.repository.GoalsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for goals editing.
 */
data class GoalsUiState(
    val goals: UserGoals = UserGoals(),
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false
)

@HiltViewModel
class GoalsViewModel @Inject constructor(
    private val goalsRepository: GoalsRepository
) : ViewModel() {
    
    val goals = goalsRepository.getGoals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    
    private val _uiState = MutableStateFlow(GoalsUiState())
    val uiState: StateFlow<GoalsUiState> = _uiState.asStateFlow()
    
    init {
        viewModelScope.launch {
            val currentGoals = goalsRepository.getGoalsOnce()
            _uiState.value = _uiState.value.copy(goals = currentGoals)
        }
    }
    
    fun updateCalories(calories: Int) {
        _uiState.value = _uiState.value.copy(
            goals = _uiState.value.goals.copy(dailyCalories = calories),
            saveSuccess = false
        )
    }
    
    fun updateProtein(grams: Int) {
        _uiState.value = _uiState.value.copy(
            goals = _uiState.value.goals.copy(proteinGrams = grams),
            saveSuccess = false
        )
    }
    
    fun updateCarbs(grams: Int) {
        _uiState.value = _uiState.value.copy(
            goals = _uiState.value.goals.copy(carbsGrams = grams),
            saveSuccess = false
        )
    }
    
    fun updateFat(grams: Int) {
        _uiState.value = _uiState.value.copy(
            goals = _uiState.value.goals.copy(fatGrams = grams),
            saveSuccess = false
        )
    }
    
    fun updateFiber(grams: Int) {
        _uiState.value = _uiState.value.copy(
            goals = _uiState.value.goals.copy(fiberGrams = grams),
            saveSuccess = false
        )
    }
    
    fun updateSodium(mg: Int) {
        _uiState.value = _uiState.value.copy(
            goals = _uiState.value.goals.copy(sodiumMg = mg),
            saveSuccess = false
        )
    }
    
    fun saveGoals() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            
            goalsRepository.saveGoals(_uiState.value.goals)
            
            _uiState.value = _uiState.value.copy(
                isSaving = false,
                saveSuccess = true
            )
        }
    }
    
    fun updateAge(age: Int) {
        _uiState.value = _uiState.value.copy(
            goals = _uiState.value.goals.copy(age = age),
            saveSuccess = false
        )
    }

    fun updateGender(gender: String) {
        _uiState.value = _uiState.value.copy(
            goals = _uiState.value.goals.copy(gender = gender),
            saveSuccess = false
        )
    }

    fun updateCurrentWeight(weight: Float) {
        _uiState.value = _uiState.value.copy(
            goals = _uiState.value.goals.copy(currentWeight = weight),
            saveSuccess = false
        )
    }

    fun updateTargetWeight(weight: Float) {
        _uiState.value = _uiState.value.copy(
            goals = _uiState.value.goals.copy(targetWeight = weight),
            saveSuccess = false
        )
    }

    fun updateHeight(height: Float) {
        _uiState.value = _uiState.value.copy(
            goals = _uiState.value.goals.copy(height = height),
            saveSuccess = false
        )
    }

    fun updateActivityLevel(level: String) {
        _uiState.value = _uiState.value.copy(
            goals = _uiState.value.goals.copy(activityLevel = level),
            saveSuccess = false
        )
    }

    fun updateWeightGainRate(rate: Float) {
        _uiState.value = _uiState.value.copy(
            goals = _uiState.value.goals.copy(weightGainRate = rate),
            saveSuccess = false
        )
    }

    fun updateWeightUnit(unit: String) {
        _uiState.value = _uiState.value.copy(
            goals = _uiState.value.goals.copy(weightUnit = unit),
            saveSuccess = false
        )
    }

    fun updateHeightUnit(unit: String) {
        _uiState.value = _uiState.value.copy(
            goals = _uiState.value.goals.copy(heightUnit = unit),
            saveSuccess = false
        )
    }

    fun calculateAndApplyGoals() {
        val goals = _uiState.value.goals
        
        // 1. Convert to Metric (kg, cm)
        val weightKg = if (goals.weightUnit == "lbs") goals.currentWeight * 0.453592f else goals.currentWeight
        val heightCm = if (goals.heightUnit == "in") goals.height * 2.54f else goals.height
        
        // 2. Mifflin-St Jeor BMR
        val bmr = if (goals.gender == "Male") {
            (10 * weightKg) + (6.25 * heightCm) - (5 * goals.age) + 5
        } else {
            (10 * weightKg) + (6.25 * heightCm) - (5 * goals.age) - 161
        }
        
        // 3. Apply Activity Multiplier
        val multiplier = when (goals.activityLevel) {
            "Sedentary" -> 1.2f
            "Light" -> 1.375f
            "Moderate" -> 1.55f
            "Very" -> 1.725f
            "Super" -> 1.9f
            else -> 1.2f
        }
        val tdee = bmr * multiplier
        
        // 4. Add Surplus for weight gain
        // 1kg/week surplus is ~1100 kcal extra/day. 1lb/week is ~500 kcal extra/day.
        val surplusPerDay = if (goals.weightUnit == "lbs") {
            goals.weightGainRate * 500f
        } else {
            goals.weightGainRate * 1100f
        }
        
        val targetCalories = (tdee + surplusPerDay).toInt()
        
        // 5. Macro Distribution (e.g., 30/40/30)
        val protein = (targetCalories * 0.30 / 4).toInt()
        val carbs = (targetCalories * 0.40 / 4).toInt()
        val fat = (targetCalories * 0.30 / 9).toInt()
        
        _uiState.value = _uiState.value.copy(
            goals = _uiState.value.goals.copy(
                dailyCalories = targetCalories,
                proteinGrams = protein,
                carbsGrams = carbs,
                fatGrams = fat
            ),
            saveSuccess = false
        )
    }
    
    /**
     * Calculate macro distribution based on calories.
     * Common split: 30% protein, 40% carbs, 30% fat
     */
    fun autoCalculateMacros() {
        val calories = _uiState.value.goals.dailyCalories
        
        // Protein: 30% of calories, 4 cal/gram
        val protein = (calories * 0.30 / 4).toInt()
        
        // Carbs: 40% of calories, 4 cal/gram
        val carbs = (calories * 0.40 / 4).toInt()
        
        // Fat: 30% of calories, 9 cal/gram
        val fat = (calories * 0.30 / 9).toInt()
        
        _uiState.value = _uiState.value.copy(
            goals = _uiState.value.goals.copy(
                proteinGrams = protein,
                carbsGrams = carbs,
                fatGrams = fat
            ),
            saveSuccess = false
        )
    }
}
