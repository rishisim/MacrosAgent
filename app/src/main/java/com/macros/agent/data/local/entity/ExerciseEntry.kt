package com.macros.agent.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Source of exercise data.
 */
enum class ExerciseSource {
    MANUAL,      // User entered manually
    GOOGLE_FIT,  // Synced from Google Fit
    STEPS        // Calculated from step count
}

/**
 * Represents an exercise/activity entry for calorie adjustment.
 * Formula: Calories Remaining = Goal - Food + Exercise
 */
@Entity(
    tableName = "exercise_entries",
    indices = [Index("date")]
)
data class ExerciseEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String, // Format: YYYY-MM-DD
    val source: ExerciseSource = ExerciseSource.MANUAL,
    val activityName: String,
    val caloriesBurned: Int,
    val durationMinutes: Int? = null,
    val steps: Int? = null, // For step-based entries
    val timestamp: Long = System.currentTimeMillis(),
    val googleFitActivityId: String? = null, // For deduplication
    val notes: String? = null
)
