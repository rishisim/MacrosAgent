package com.macros.agent.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_meals")
data class UserMeal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val totalCalories: Float,
    val totalProtein: Float,
    val totalCarbs: Float,
    val totalFat: Float,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_meal_items")
data class UserMealItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val mealId: Long,
    val foodName: String,
    val servingSize: Float,
    val servingUnit: String,
    val servingsConsumed: Float,
    val calories: Float,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val fdcId: Int? = null
)
