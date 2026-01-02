package com.macros.agent.ui.screens.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.macros.agent.data.local.dao.UserMealWithItems
import com.macros.agent.data.local.entity.DiaryEntry
import com.macros.agent.data.local.entity.Food
import com.macros.agent.data.local.entity.MealType
import com.macros.agent.data.repository.DiaryRepository
import com.macros.agent.data.repository.FoodRepository
import com.macros.agent.data.repository.UserMealRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Search result state.
 */
sealed class SearchState {
    data object Idle : SearchState()
    data object Loading : SearchState()
    data class Success(val foods: List<Food>) : SearchState()
    data class Error(val message: String) : SearchState()
}

/**
 * UI state for Search screen.
 */
data class SearchUiState(
    val query: String = "",
    val searchState: SearchState = SearchState.Idle,
    val recentFoods: List<Food> = emptyList(),
    val favorites: List<Food> = emptyList(),
    val customMeals: List<UserMealWithItems> = emptyList(),
    val showRecentsAndFavorites: Boolean = true,
    val date: String? = null
)

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val foodRepository: FoodRepository,
    private val userMealRepository: UserMealRepository,
    private val diaryRepository: DiaryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val dateArg: String? = savedStateHandle["date"]
    
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    
    // Observe recent foods
    val recentFoods = foodRepository.getRecentFoods(20)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    // Observe favorites
    val favorites = foodRepository.getFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    init {
        // Debounced search
        viewModelScope.launch {
            _searchQuery
                .debounce(300)
                .distinctUntilChanged()
                .collect { query ->
                    if (query.isBlank()) {
                        _uiState.value = _uiState.value.copy(
                            searchState = SearchState.Idle,
                            showRecentsAndFavorites = true
                        )
                    } else {
                        performSearch(query)
                    }
                }
        }
        
        // Update recents and favorites in UI state
        viewModelScope.launch {
            recentFoods.collect { foods ->
                _uiState.value = _uiState.value.copy(recentFoods = foods)
            }
        }
        
        viewModelScope.launch {
            favorites.collect { foods ->
                _uiState.value = _uiState.value.copy(favorites = foods)
            }
        }

        viewModelScope.launch {
            userMealRepository.getAllMeals().collect { meals ->
                _uiState.value = _uiState.value.copy(
                    customMeals = meals,
                    date = dateArg
                )
            }
        }
    }
    
    fun updateQuery(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
        _searchQuery.value = query
    }
    
    fun clearSearch() {
        _uiState.value = _uiState.value.copy(
            query = "",
            searchState = SearchState.Idle,
            showRecentsAndFavorites = true
        )
        _searchQuery.value = ""
    }
    
    private suspend fun performSearch(query: String) {
        _uiState.value = _uiState.value.copy(
            searchState = SearchState.Loading,
            showRecentsAndFavorites = false
        )
        
        val result = foodRepository.searchFoods(query)
        
        _uiState.value = _uiState.value.copy(
            searchState = result.fold(
                onSuccess = { foods ->
                    if (foods.isEmpty()) {
                        SearchState.Error("No foods found for \"$query\"")
                    } else {
                        SearchState.Success(foods)
                    }
                },
                onFailure = { error ->
                    SearchState.Error(error.message ?: "Search failed")
                }
            )
        )
    }
    
    fun searchByBarcode(barcode: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                searchState = SearchState.Loading
            )
            
            val result = foodRepository.searchByBarcode(barcode)
            
            _uiState.value = _uiState.value.copy(
                searchState = result.fold(
                    onSuccess = { food ->
                        if (food != null) {
                            SearchState.Success(listOf(food))
                        } else {
                            SearchState.Error("No product found for barcode: $barcode")
                        }
                    },
                    onFailure = { error ->
                        SearchState.Error(error.message ?: "Barcode search failed")
                    }
                )
            )
        }
    }
    
    fun toggleFavorite(food: Food) {
        viewModelScope.launch {
            foodRepository.toggleFavorite(food.fdcId, !food.isFavorite)
        }
    }
    
    fun onFoodSelected(food: Food) {
        viewModelScope.launch {
            foodRepository.updateFoodUsage(food.fdcId)
        }
    }

    fun addMealToLog(mealWithItems: UserMealWithItems, mealType: MealType) {
        val dateString = _uiState.value.date ?: diaryRepository.formatDate(java.time.LocalDate.now())
        viewModelScope.launch {
            val entries = mealWithItems.items.map { item ->
                DiaryEntry(
                    date = dateString,
                    mealType = mealType,
                    foodName = item.foodName,
                    servingSize = item.servingSize,
                    servingUnit = item.servingUnit,
                    servingsConsumed = item.servingsConsumed,
                    calories = item.calories,
                    protein = item.protein,
                    carbs = item.carbs,
                    fat = item.fat,
                    fdcId = item.fdcId
                )
            }
            diaryRepository.addEntries(entries)
        }
    }
}
