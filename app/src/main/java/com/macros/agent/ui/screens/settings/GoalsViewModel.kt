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
