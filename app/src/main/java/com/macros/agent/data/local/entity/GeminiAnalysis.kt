package com.macros.agent.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Stores Gemini Vision analysis results for food photos.
 */
@Entity(tableName = "gemini_analyses")
data class GeminiAnalysis(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val photoUri: String,
    val timestamp: Long = System.currentTimeMillis(),
    val rawResponse: String, // Full JSON response for debugging
    val detectedFoodsJson: String, // JSON array of detected food items
    val totalCalories: Float,
    val totalProtein: Float,
    val totalCarbs: Float,
    val totalFat: Float,
    val confidence: Float, // 0.0 to 1.0
    val wasAccepted: Boolean = false, // User confirmed the analysis
    val wasModified: Boolean = false // User edited the results
)
