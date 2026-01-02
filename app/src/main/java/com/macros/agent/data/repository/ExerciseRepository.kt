package com.macros.agent.data.repository

import com.macros.agent.data.local.dao.ExerciseDao
import com.macros.agent.data.local.entity.ExerciseEntry
import com.macros.agent.data.local.entity.ExerciseSource
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for exercise entries.
 * Handles manual entries and future Google Fit integration.
 */
@Singleton
class ExerciseRepository @Inject constructor(
    private val exerciseDao: ExerciseDao
) {
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    
    fun formatDate(date: LocalDate): String = date.format(dateFormatter)
    
    /**
     * Get all exercise entries for a date.
     */
    fun getEntriesForDate(date: LocalDate): Flow<List<ExerciseEntry>> {
        return exerciseDao.getEntriesForDate(formatDate(date))
    }
    
    /**
     * Get total calories burned for a date.
     */
    fun getTotalCaloriesBurned(date: LocalDate): Flow<Int> {
        return exerciseDao.getTotalCaloriesBurned(formatDate(date))
    }
    
    /**
     * Add a manual exercise entry.
     */
    suspend fun addManualExercise(
        date: LocalDate,
        activityName: String,
        caloriesBurned: Int,
        durationMinutes: Int? = null,
        notes: String? = null
    ): Long {
        val entry = ExerciseEntry(
            date = formatDate(date),
            source = ExerciseSource.MANUAL,
            activityName = activityName,
            caloriesBurned = caloriesBurned,
            durationMinutes = durationMinutes,
            notes = notes
        )
        return exerciseDao.insert(entry)
    }
    
    /**
     * Add step-based exercise entry.
     */
    suspend fun addStepsEntry(
        date: LocalDate,
        steps: Int,
        caloriesBurned: Int
    ): Long {
        // Delete existing steps entry for date to avoid duplicates
        exerciseDao.deleteBySourceForDate(ExerciseSource.STEPS, formatDate(date))
        
        val entry = ExerciseEntry(
            date = formatDate(date),
            source = ExerciseSource.STEPS,
            activityName = "Walking",
            caloriesBurned = caloriesBurned,
            steps = steps
        )
        return exerciseDao.insert(entry)
    }
    
    /**
     * Delete an exercise entry.
     */
    suspend fun deleteEntry(entry: ExerciseEntry) {
        exerciseDao.delete(entry)
    }
    
    suspend fun deleteEntryById(id: Long) {
        exerciseDao.deleteById(id)
    }
    
    /**
     * Get entry by ID.
     */
    suspend fun getEntryById(id: Long): ExerciseEntry? {
        return exerciseDao.getById(id)
    }
    
    /**
     * Update an exercise entry.
     */
    suspend fun updateEntry(entry: ExerciseEntry) {
        exerciseDao.update(entry)
    }
    
    @Inject
    lateinit var googleFitService: com.macros.agent.data.remote.api.GoogleFitService

    suspend fun syncFromGoogleFit(date: LocalDate) {
        if (!googleFitService.hasPermissions()) {
            return
        }
        
        val steps = googleFitService.getDailySteps(date)
        if (steps > 0) {
            // Rough estimate: 0.04 calories per step
            val calories = (steps * 0.04).toInt()
            addStepsEntry(date, steps, calories)
        }
    }
}
