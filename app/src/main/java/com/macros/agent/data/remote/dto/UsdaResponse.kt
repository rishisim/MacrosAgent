package com.macros.agent.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * USDA FoodData Central API response DTOs.
 * API Docs: https://fdc.nal.usda.gov/api-guide.html
 */

// Search response
data class UsdaSearchResponse(
    @SerializedName("foods") val foods: List<UsdaSearchFood>,
    @SerializedName("totalHits") val totalHits: Int,
    @SerializedName("currentPage") val currentPage: Int,
    @SerializedName("totalPages") val totalPages: Int
)

data class UsdaSearchFood(
    @SerializedName("fdcId") val fdcId: Int,
    @SerializedName("description") val description: String,
    @SerializedName("dataType") val dataType: String?,
    @SerializedName("brandOwner") val brandOwner: String?,
    @SerializedName("brandName") val brandName: String?,
    @SerializedName("gtinUpc") val gtinUpc: String?,
    @SerializedName("ingredients") val ingredients: String?,
    @SerializedName("foodCategory") val foodCategory: String?,
    @SerializedName("servingSize") val servingSize: Float?,
    @SerializedName("servingSizeUnit") val servingSizeUnit: String?,
    @SerializedName("foodNutrients") val foodNutrients: List<UsdaFoodNutrient>?
)

data class UsdaFoodNutrient(
    @SerializedName("nutrientId") val nutrientId: Int?,
    @SerializedName("nutrientName") val nutrientName: String?,
    @SerializedName("nutrientNumber") val nutrientNumber: String?,
    @SerializedName("unitName") val unitName: String?,
    @SerializedName("value") val value: Float?
)

// Food detail response
data class UsdaFoodDetailResponse(
    @SerializedName("fdcId") val fdcId: Int,
    @SerializedName("description") val description: String,
    @SerializedName("dataType") val dataType: String?,
    @SerializedName("brandOwner") val brandOwner: String?,
    @SerializedName("brandName") val brandName: String?,
    @SerializedName("gtinUpc") val gtinUpc: String?,
    @SerializedName("ingredients") val ingredients: String?,
    @SerializedName("foodCategory") val foodCategory: String?,
    @SerializedName("servingSize") val servingSize: Float?,
    @SerializedName("servingSizeUnit") val servingSizeUnit: String?,
    @SerializedName("foodNutrients") val foodNutrients: List<UsdaDetailNutrient>?,
    @SerializedName("labelNutrients") val labelNutrients: UsdaLabelNutrients?
)

data class UsdaDetailNutrient(
    @SerializedName("nutrient") val nutrient: UsdaNutrientInfo?,
    @SerializedName("amount") val amount: Float?
)

data class UsdaNutrientInfo(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("number") val number: String?,
    @SerializedName("unitName") val unitName: String?
)

data class UsdaLabelNutrients(
    @SerializedName("calories") val calories: UsdaNutrientValue?,
    @SerializedName("protein") val protein: UsdaNutrientValue?,
    @SerializedName("carbohydrates") val carbohydrates: UsdaNutrientValue?,
    @SerializedName("fat") val fat: UsdaNutrientValue?,
    @SerializedName("fiber") val fiber: UsdaNutrientValue?,
    @SerializedName("sugars") val sugars: UsdaNutrientValue?,
    @SerializedName("sodium") val sodium: UsdaNutrientValue?
)

data class UsdaNutrientValue(
    @SerializedName("value") val value: Float?
)

/**
 * USDA Nutrient IDs for common macros.
 * Reference: https://fdc.nal.usda.gov/api-guide.html
 */
object UsdaNutrientIds {
    const val ENERGY_KCAL = 1008      // Energy (kcal)
    const val PROTEIN = 1003          // Protein
    const val TOTAL_FAT = 1004        // Total lipid (fat)
    const val CARBOHYDRATES = 1005    // Carbohydrate, by difference
    const val FIBER = 1079            // Fiber, total dietary
    const val SUGARS = 2000           // Sugars, total including NLEA
    const val SODIUM = 1093           // Sodium, Na
}
