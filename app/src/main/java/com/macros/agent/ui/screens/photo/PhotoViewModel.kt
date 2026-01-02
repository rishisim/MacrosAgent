package com.macros.agent.ui.screens.photo

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.macros.agent.data.local.dao.GeminiAnalysisDao
import com.macros.agent.data.local.entity.DiaryEntry
import com.macros.agent.data.local.entity.GeminiAnalysis
import com.macros.agent.data.local.entity.MealType
import com.macros.agent.data.remote.api.DetectedFood
import com.macros.agent.data.remote.api.FoodAnalysisResult
import com.macros.agent.data.remote.api.GeminiService
import com.macros.agent.data.repository.DiaryRepository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

/**
 * Analysis state.
 */
sealed class AnalysisState {
    data object Idle : AnalysisState()
    data object Capturing : AnalysisState()
    data object Analyzing : AnalysisState()
    data class Success(val result: FoodAnalysisResult) : AnalysisState()
    data class Error(val message: String) : AnalysisState()
}

/**
 * UI state for Photo screen.
 */
data class PhotoUiState(
    val analysisState: AnalysisState = AnalysisState.Idle,
    val detectedFoods: List<DetectedFood> = emptyList(),
    val selectedMealType: MealType = MealType.LUNCH,
    val photoUri: String? = null,
    val isAddingToLog: Boolean = false,
    val userContext: String = ""
)

@HiltViewModel
class PhotoViewModel @Inject constructor(
    private val geminiService: GeminiService,
    private val diaryRepository: DiaryRepository,
    private val geminiAnalysisDao: GeminiAnalysisDao
) : ViewModel() {
    
    private val gson = Gson()
    
    private val _uiState = MutableStateFlow(PhotoUiState())
    val uiState: StateFlow<PhotoUiState> = _uiState.asStateFlow()
    
    /**
     * Analyze a food photo.
     */
    fun analyzePhoto(bitmap: Bitmap, photoUri: String? = null) {
        val userContext = _uiState.value.userContext
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                analysisState = AnalysisState.Analyzing,
                photoUri = photoUri
            )
            
            val result = geminiService.analyzeFood(bitmap, userContext)
            
            if (result.success) {
                // Save analysis to database
                val analysis = GeminiAnalysis(
                    photoUri = photoUri ?: "",
                    rawResponse = result.rawResponse,
                    detectedFoodsJson = gson.toJson(result.detectedFoods),
                    totalCalories = result.totalCalories,
                    totalProtein = result.totalProtein,
                    totalCarbs = result.totalCarbs,
                    totalFat = result.totalFat,
                    confidence = result.overallConfidence
                )
                geminiAnalysisDao.insert(analysis)
                
                _uiState.value = _uiState.value.copy(
                    analysisState = AnalysisState.Success(result),
                    detectedFoods = result.detectedFoods
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    analysisState = AnalysisState.Error(
                        result.errorMessage ?: "Analysis failed"
                    )
                )
            }
        }
    }
    
    /**
     * Update meal type for adding entries.
     */
    fun selectMealType(mealType: MealType) {
        _uiState.value = _uiState.value.copy(selectedMealType = mealType)
    }

    fun updateUserContext(context: String) {
        _uiState.value = _uiState.value.copy(userContext = context)
    }
    
    /**
     * Update a detected food (user can edit estimates).
     */
    fun updateDetectedFood(index: Int, updatedFood: DetectedFood) {
        val currentFoods = _uiState.value.detectedFoods.toMutableList()
        if (index in currentFoods.indices) {
            currentFoods[index] = updatedFood
            _uiState.value = _uiState.value.copy(detectedFoods = currentFoods)
        }
    }
    
    /**
     * Remove a detected food from the list.
     */
    fun removeDetectedFood(index: Int) {
        val currentFoods = _uiState.value.detectedFoods.toMutableList()
        if (index in currentFoods.indices) {
            currentFoods.removeAt(index)
            _uiState.value = _uiState.value.copy(detectedFoods = currentFoods)
        }
    }
    
    /**
     * Add all detected foods to the diary.
     */
    fun addToLog(date: LocalDate = LocalDate.now()) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isAddingToLog = true)
            
            val entries = _uiState.value.detectedFoods.map { food ->
                DiaryEntry(
                    date = diaryRepository.formatDate(date),
                    mealType = _uiState.value.selectedMealType,
                    foodName = food.name,
                    servingSize = food.estimatedPortionGrams,
                    servingUnit = "g",
                    servingsConsumed = 1f,
                    calories = food.calories,
                    protein = food.protein,
                    carbs = food.carbs,
                    fat = food.fat,
                    photoUri = _uiState.value.photoUri,
                    isFromGemini = true
                )
            }
            
            diaryRepository.addEntries(entries)
            
            // Reset state
            _uiState.value = PhotoUiState()
        }
    }
    
    /**
     * Reset to start a new analysis.
     */
    fun reset() {
        _uiState.value = PhotoUiState()
    }
}
