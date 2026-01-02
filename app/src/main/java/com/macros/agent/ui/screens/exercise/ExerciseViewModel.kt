package com.macros.agent.ui.screens.exercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.macros.agent.data.local.entity.ExerciseEntry
import com.macros.agent.data.repository.ExerciseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class ExerciseUiState(
    val entries: List<ExerciseEntry> = emptyList(),
    val totalCalories: Int = 0,
    val selectedDate: LocalDate = LocalDate.now(),
    val isLoading: Boolean = true
)

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExerciseUiState())
    val uiState: StateFlow<ExerciseUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        val date = _uiState.value.selectedDate
        viewModelScope.launch {
            exerciseRepository.getEntriesForDate(date).collect { entries ->
                _uiState.value = _uiState.value.copy(
                    entries = entries,
                    isLoading = false
                )
            }
        }
        
        viewModelScope.launch {
            exerciseRepository.getTotalCaloriesBurned(date).collect { calories ->
                _uiState.value = _uiState.value.copy(
                    totalCalories = calories
                )
            }
        }
    }

    fun addExercise(name: String, calories: Int, duration: Int?) {
        viewModelScope.launch {
            exerciseRepository.addManualExercise(
                date = _uiState.value.selectedDate,
                activityName = name,
                caloriesBurned = calories,
                durationMinutes = duration
            )
        }
    }

    fun deleteExercise(entry: ExerciseEntry) {
        viewModelScope.launch {
            exerciseRepository.deleteEntry(entry)
        }
    }
    fun syncWithGoogleFit(context: android.content.Context) {
        viewModelScope.launch {
            exerciseRepository.syncFromGoogleFit(_uiState.value.selectedDate)
        }
    }
}
