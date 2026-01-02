package com.macros.agent.ui.screens.food

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.macros.agent.data.local.entity.DiaryEntry
import com.macros.agent.data.local.entity.Food
import com.macros.agent.data.local.entity.MealType
import com.macros.agent.data.repository.DiaryRepository
import com.macros.agent.data.repository.FoodRepository
import com.macros.agent.data.remote.api.GeminiService
import com.macros.agent.util.UnitConverter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class FoodDetailUiState(
    val food: Food? = null,
    val isLoading: Boolean = true,
    val selectedMealType: MealType = MealType.SNACKS,
    val servingQuantity: Float = 1f, // Number of servings
    val servingQuantityString: String = "1", // Raw input for the text field
    val servingSize: Float = 100f,   // Grams per serving (defaults to food.servingSize)
    val servingUnit: String = "g",
    val isSaved: Boolean = false,
    val isEditMode: Boolean = false,
    val isAnalyzing: Boolean = false,
    val entryId: Long? = null,
    val date: String? = null
) {
    // Calculated Macros
    val totalCalories: Float get() {
        val totalGrams = UnitConverter.getWeightInGrams(servingQuantity, servingUnit, food?.servingSize ?: 100f)
        val multiplier = totalGrams / 100f
        return (food?.calories ?: 0f) * multiplier
    }
    
    val totalProtein: Float get() {
        val totalGrams = UnitConverter.getWeightInGrams(servingQuantity, servingUnit, food?.servingSize ?: 100f)
        val multiplier = totalGrams / 100f
        return (food?.protein ?: 0f) * multiplier
    }
    
    val totalCarbs: Float get() {
        val totalGrams = UnitConverter.getWeightInGrams(servingQuantity, servingUnit, food?.servingSize ?: 100f)
        val multiplier = totalGrams / 100f
        return (food?.carbs ?: 0f) * multiplier
    }
    
    val totalFat: Float get() {
        val totalGrams = UnitConverter.getWeightInGrams(servingQuantity, servingUnit, food?.servingSize ?: 100f)
        val multiplier = totalGrams / 100f
        return (food?.fat ?: 0f) * multiplier
    }
}

@HiltViewModel
class FoodDetailViewModel @Inject constructor(
    private val foodRepository: FoodRepository,
    private val diaryRepository: DiaryRepository,
    private val geminiService: com.macros.agent.data.remote.api.GeminiService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val fdcId: Int? = savedStateHandle.get<Int>("fdcId")
    private val entryId: Long? = savedStateHandle.get<Long>("entryId")
    private val mealTypeArg: String? = savedStateHandle["mealType"]
    private val dateArg: String? = savedStateHandle["date"]

    private val _uiState = MutableStateFlow(FoodDetailUiState())
    val uiState: StateFlow<FoodDetailUiState> = _uiState.asStateFlow()

    init {
        // Parse meal type
        val mealType = try {
            if (mealTypeArg != null) MealType.valueOf(mealTypeArg) else MealType.SNACKS
        } catch (e: Exception) {
            MealType.SNACKS
        }
        
        _uiState.value = _uiState.value.copy(
            selectedMealType = mealType,
            date = if (dateArg.isNullOrBlank()) null else dateArg
        )

        if (entryId != null && entryId != -1L) {
            loadExistingEntry(entryId)
        } else if (fdcId != null && fdcId != -1) {
            loadFood(fdcId)
        }
    }

    private fun loadExistingEntry(id: Long) {
        viewModelScope.launch {
            val entry = diaryRepository.getEntryById(id)
            if (entry != null) {
                // If the entry has an fdcId, try to load the full food data for nutrients
                if (entry.fdcId != null) {
                    val food = foodRepository.getFood(entry.fdcId)
                    _uiState.value = _uiState.value.copy(
                        food = food,
                        isEditMode = true,
                        entryId = id,
                        selectedMealType = entry.mealType,
                        servingQuantity = entry.servingsConsumed,
                        servingQuantityString = if (entry.servingsConsumed % 1.0f == 0f) 
                            entry.servingsConsumed.toInt().toString() 
                        else 
                            entry.servingsConsumed.toString(),
                        servingSize = entry.servingSize,
                        servingUnit = entry.servingUnit,
                        isLoading = false
                    )
                } else {
                    // Quick add or custom entry without fdcId
                    // We can synthesize a Food object or handle it separately
                    // For now, let's treat it as a custom food
                    _uiState.value = _uiState.value.copy(
                        food = Food(
                            fdcId = -1,
                            description = entry.foodName,
                            calories = entry.calories / entry.servingsConsumed * (100f / entry.servingSize),
                            protein = entry.protein / entry.servingsConsumed * (100f / entry.servingSize),
                            carbs = entry.carbs / entry.servingsConsumed * (100f / entry.servingSize),
                            fat = entry.fat / entry.servingsConsumed * (100f / entry.servingSize)
                        ),
                        isEditMode = true,
                        entryId = id,
                        selectedMealType = entry.mealType,
                        servingQuantity = entry.servingsConsumed,
                        servingQuantityString = if (entry.servingsConsumed % 1.0f == 0f) 
                            entry.servingsConsumed.toInt().toString() 
                        else 
                            entry.servingsConsumed.toString(),
                        servingSize = entry.servingSize,
                        servingUnit = entry.servingUnit,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun loadFood(id: Int) {
        viewModelScope.launch {
            foodRepository.getFoodFlow(id).collect { food ->
                if (food != null) {
                    _uiState.value = _uiState.value.copy(
                        food = food,
                        isLoading = false,
                        servingSize = food.servingSize,
                        servingUnit = food.servingUnit
                    )
                }
            }
        }
    }

    fun updateQuantity(input: String) {
        val quantity = input.toFloatOrNull()
        if (quantity != null && quantity >= 0) {
            _uiState.value = _uiState.value.copy(
                servingQuantity = quantity,
                servingQuantityString = input
            )
        } else {
            _uiState.value = _uiState.value.copy(
                servingQuantityString = input
            )
        }
    }
    
    fun incrementQuantity(delta: Float) {
        val newQuantity = (_uiState.value.servingQuantity + delta).coerceAtLeast(0.5f)
        updateQuantity(if (newQuantity % 1.0f == 0f) newQuantity.toInt().toString() else newQuantity.toString())
    }
    
    fun updateServingSize(size: Float) {
        if (size > 0) {
            _uiState.value = _uiState.value.copy(servingSize = size)
        }
    }
    
    fun updateMealType(mealType: MealType) {
        _uiState.value = _uiState.value.copy(selectedMealType = mealType)
    }

    fun updateServingUnit(unit: String) {
        _uiState.value = _uiState.value.copy(servingUnit = unit)
    }

    fun adjustWithAI(request: String) {
        val currentFood = _uiState.value.food ?: return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isAnalyzing = true)
            
            val originalMacros = com.macros.agent.data.remote.api.DetectedFood(
                name = currentFood.description,
                estimatedPortionSize = "${_uiState.value.servingSize} ${_uiState.value.servingUnit}",
                estimatedPortionGrams = _uiState.value.servingSize,
                calories = currentFood.calories * (_uiState.value.servingSize / 100f),
                protein = currentFood.protein * (_uiState.value.servingSize / 100f),
                carbs = currentFood.carbs * (_uiState.value.servingSize / 100f),
                fat = currentFood.fat * (_uiState.value.servingSize / 100f),
                confidence = 1.0f
            )
            
            val adjusted = geminiService.adjustMacros(
                foodName = currentFood.description,
                originalMacros = originalMacros,
                adjustmentRequest = request
            )
            
            _uiState.value = _uiState.value.copy(
                isAnalyzing = false,
                servingQuantity = 1.0f,
                servingQuantityString = "1",
                servingSize = adjusted.estimatedPortionGrams,
                servingUnit = adjusted.estimatedPortionSize
            )
        }
    }

    fun addToDiary() {
        val state = uiState.value
        val food = state.food ?: return
        
        viewModelScope.launch {
            val entryDate = state.date ?: diaryRepository.formatDate(LocalDate.now())
            val entry = DiaryEntry(
                id = state.entryId ?: 0,
                date = entryDate,
                mealType = state.selectedMealType,
                foodName = food.description,
                servingSize = state.servingSize,
                servingUnit = state.servingUnit,
                servingsConsumed = state.servingQuantity,
                calories = state.totalCalories,
                protein = state.totalProtein,
                carbs = state.totalCarbs,
                fat = state.totalFat,
                fdcId = if (food.fdcId != -1) food.fdcId else null
            )
            
            if (state.isEditMode) {
                diaryRepository.updateEntry(entry)
            } else {
                diaryRepository.addEntries(listOf(entry))
                if (food.fdcId != -1) {
                    foodRepository.updateFoodUsage(food.fdcId)
                }
            }
            
            _uiState.value = _uiState.value.copy(isSaved = true)
        }
    }

    fun deleteEntry() {
        val entryId = uiState.value.entryId ?: return
        viewModelScope.launch {
            diaryRepository.deleteEntryById(entryId)
            _uiState.value = _uiState.value.copy(isSaved = true)
        }
    }
}
