package com.macros.agent.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a food item from USDA database or custom entry.
 * FdcId is the USDA FoodData Central ID for database foods.
 * For custom foods, we use negative IDs.
 */
@Entity(tableName = "foods")
data class Food(
    @PrimaryKey
    val fdcId: Int,
    val description: String,
    val brandOwner: String? = null,
    val brandName: String? = null,
    val calories: Float = 0f,
    val protein: Float = 0f,
    val carbs: Float = 0f,
    val fat: Float = 0f,
    val fiber: Float = 0f,
    val sugar: Float = 0f,
    val sodium: Float = 0f,
    val servingSize: Float = 100f,
    val servingUnit: String = "g",
    val category: String? = null,
    val ingredients: String? = null,
    val barcode: String? = null,
    val lastUsed: Long = System.currentTimeMillis(),
    val useCount: Int = 0,
    val isFavorite: Boolean = false,
    val isCustom: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
