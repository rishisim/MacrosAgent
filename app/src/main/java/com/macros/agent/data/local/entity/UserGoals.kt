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
    val dailyCalories: Int = 2000,
    val proteinGrams: Int = 150,
    val carbsGrams: Int = 200,
    val fatGrams: Int = 65,
    val fiberGrams: Int = 30,
    val sugarGrams: Int = 50,
    val sodiumMg: Int = 2300,
    val updatedAt: Long = System.currentTimeMillis()
)
