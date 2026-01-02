package com.macros.agent.ui.screens.diary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.macros.agent.data.local.dao.DateCalorie
import com.macros.agent.data.repository.DiaryRepository
import com.macros.agent.data.repository.GoalsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import javax.inject.Inject

data class ProgressChartUiState(
    val history: List<DateCalorie> = emptyList(),
    val calorieGoal: Int = 2000,
    val isLoading: Boolean = true
)

@HiltViewModel
class ProgressChartViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository,
    private val goalsRepository: GoalsRepository
) : ViewModel() {

    private val _daysRange = MutableStateFlow(7)
    val daysRange: StateFlow<Int> = _daysRange.asStateFlow()

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<ProgressChartUiState> = _daysRange
        .flatMapLatest { days ->
            val endDate = LocalDate.now()
            val startDate = endDate.minusDays(days.toLong() - 1)
            
            combine(
                diaryRepository.getCalorieHistory(startDate, endDate),
                goalsRepository.getGoals()
            ) { history, goals ->
                ProgressChartUiState(
                    history = history,
                    calorieGoal = goals?.dailyCalories ?: 2000,
                    isLoading = false
                )
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProgressChartUiState())

    fun updateRange(days: Int) {
        _daysRange.value = days
    }
}
