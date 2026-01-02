package com.macros.agent.util

/**
 * Utility class for converting various food units to grams.
 * Note: Volume conversions (cup, tbsp, tsp) are estimates based on water density
 * unless more specific data is available.
 */
object UnitConverter {

    // Common weight conversions to grams
    private const val GRAMS_PER_OZ = 28.3495f
    private const val GRAMS_PER_LB = 453.592f
    
    // Common volume conversions to ml (approx grams for most moist foods)
    private const val ML_PER_CUP = 240f
    private const val ML_PER_TBSP = 14.7868f
    private const val ML_PER_TSP = 4.92892f

    /**
     * Converts a quantity and unit to an equivalent weight in grams.
     * @param quantity The amount of the unit
     * @param unit The unit name (e.g., "oz", "lbs", "cup", "g")
     * @param defaultServingSize The fallback weight in grams if the unit is unknown (e.g., "piece")
     * @return The weight in grams
     */
    fun getWeightInGrams(quantity: Float, unit: String, defaultServingSize: Float = 100f): Float {
        val normalizedUnit = unit.lowercase().trim()
        
        return when {
            normalizedUnit == "g" || normalizedUnit == "gram" || normalizedUnit == "grams" -> {
                quantity
            }
            normalizedUnit == "oz" || normalizedUnit == "ounce" || normalizedUnit == "ounces" -> {
                quantity * GRAMS_PER_OZ
            }
            normalizedUnit == "lb" || normalizedUnit == "lbs" || normalizedUnit == "pound" || normalizedUnit == "pounds" -> {
                quantity * GRAMS_PER_LB
            }
            normalizedUnit == "cup" || normalizedUnit == "cups" -> {
                quantity * ML_PER_CUP
            }
            normalizedUnit == "ml" || normalizedUnit == "milliliter" || normalizedUnit == "milliliters" -> {
                quantity // 1ml approx 1g
            }
            normalizedUnit == "tbsp" || normalizedUnit == "tablespoon" || normalizedUnit == "tablespoons" -> {
                quantity * ML_PER_TBSP
            }
            normalizedUnit == "tsp" || normalizedUnit == "teaspoon" || normalizedUnit == "teaspoons" -> {
                quantity * ML_PER_TSP
            }
            normalizedUnit == "piece" || normalizedUnit == "unit" || normalizedUnit == "serving" -> {
                quantity * defaultServingSize
            }
            else -> {
                // Unknown unit, fallback to default serving size (often 100g or the food's specific serving)
                quantity * defaultServingSize
            }
        }
    }
}
