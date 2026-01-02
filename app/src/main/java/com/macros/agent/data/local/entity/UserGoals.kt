package com.macros.agent.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * User's daily macro goals.
 * Only one row expected (id = 1).
 */
@Entity(tableName = "user_goals")
data class UserGoals(
    @PrimaryKey
    val id: Int = 1,
    // Macro Goals
    val dailyCalories: Int = 2000,
    val proteinGrams: Int = 150,
    val carbsGrams: Int = 200,
    val fatGrams: Int = 65,
    val fiberGrams: Int = 30,
    val sugarGrams: Int = 50,
    val sodiumMg: Int = 2300,
    
    // Profile Fields (for BMR/TDEE)
    val age: Int = 25,
    val gender: String = "Male", // Male, Female
    val currentWeight: Float = 70f,
    val targetWeight: Float = 75f,
    val height: Float = 175f,
    val activityLevel: String = "Moderate", // Sedentary, Light, Moderate, Very, Super
    val weightGainRate: Float = 0.25f, // kg or lbs per week
    
    // Unit Preferences
    val weightUnit: String = "kg", // kg, lbs
    val heightUnit: String = "cm", // cm, in
    
    val updatedAt: Long = System.currentTimeMillis()
)
