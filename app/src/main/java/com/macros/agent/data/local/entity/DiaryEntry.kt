package com.macros.agent.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Meal type enum for diary entries.
 */
enum class MealType(val displayName: String, val order: Int) {
    BREAKFAST("Breakfast", 0),
    LUNCH("Lunch", 1),
    DINNER("Dinner", 2),
    SNACKS("Snacks", 3)
}

/**
 * Represents a food entry in the user's diary.
 * Stores denormalized macro data for performance (no joins needed for daily totals).
 */
@Entity(
    tableName = "diary_entries",
    foreignKeys = [
        ForeignKey(
            entity = Food::class,
            parentColumns = ["fdcId"],
            childColumns = ["fdcId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("date"),
        Index("fdcId"),
        Index("mealType")
    ]
)
data class DiaryEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String, // Format: YYYY-MM-DD
    val mealType: MealType,
    val fdcId: Int? = null, // Nullable for quick-add entries
    val foodName: String,
    val servingSize: Float,
    val servingUnit: String,
    val servingsConsumed: Float = 1f,
    // Denormalized macro data (calculated: nutrient * servingsConsumed)
    val calories: Float,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val timestamp: Long = System.currentTimeMillis(),
    val photoUri: String? = null,
    val notes: String? = null,
    val isFromGemini: Boolean = false,
    val geminiAnalysisId: Long? = null
)
