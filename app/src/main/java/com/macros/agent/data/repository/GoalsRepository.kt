package com.macros.agent.data.repository

import com.macros.agent.data.local.dao.GoalsDao
import com.macros.agent.data.local.entity.UserGoals
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for user goals.
 */
@Singleton
class GoalsRepository @Inject constructor(
    private val goalsDao: GoalsDao
) {
    /**
     * Get user goals as a Flow.
     */
    fun getGoals(): Flow<UserGoals?> = goalsDao.getGoals()
    
    /**
     * Get user goals once (suspend).
     */
    suspend fun getGoalsOnce(): UserGoals {
        return goalsDao.getGoalsOnce() ?: UserGoals()
    }
    
    /**
     * Save/update user goals.
     */
    suspend fun saveGoals(goals: UserGoals) {
        goalsDao.insert(goals.copy(
            id = 1, // Always use ID 1 for singleton
            updatedAt = System.currentTimeMillis()
        ))
    }
    
    /**
     * Update just the calorie goal.
     */
    suspend fun updateCalorieGoal(calories: Int) {
        val current = getGoalsOnce()
        saveGoals(current.copy(dailyCalories = calories))
    }
    
    /**
     * Update macro goals (protein, carbs, fat).
     */
    suspend fun updateMacroGoals(protein: Int, carbs: Int, fat: Int) {
        val current = getGoalsOnce()
        saveGoals(current.copy(
            proteinGrams = protein,
            carbsGrams = carbs,
            fatGrams = fat
        ))
    }
}
