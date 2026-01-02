package com.macros.agent.util

import org.junit.Assert.assertEquals
import org.junit.Test

class UnitConverterTest {

    @Test
    fun testGrams() {
        assertEquals(100f, UnitConverter.getWeightInGrams(100f, "g"), 0.01f)
        assertEquals(50f, UnitConverter.getWeightInGrams(50f, "gram"), 0.01f)
        assertEquals(25f, UnitConverter.getWeightInGrams(25f, "Grams"), 0.01f)
    }

    @Test
    fun testOunces() {
        // 1 oz approx 28.35g
        assertEquals(28.3495f, UnitConverter.getWeightInGrams(1f, "oz"), 0.01f)
        assertEquals(56.699f, UnitConverter.getWeightInGrams(2f, "ounce"), 0.01f)
    }

    @Test
    fun testPounds() {
        // 1 lb approx 453.59g
        assertEquals(453.592f, UnitConverter.getWeightInGrams(1f, "lbs"), 0.01f)
        assertEquals(907.184f, UnitConverter.getWeightInGrams(2f, "lb"), 0.01f)
    }

    @Test
    fun testCups() {
        // 1 cup approx 240g
        assertEquals(240f, UnitConverter.getWeightInGrams(1f, "cup"), 0.01f)
        assertEquals(480f, UnitConverter.getWeightInGrams(2f, "cups"), 0.01f)
    }

    @Test
    fun testTbspAndTsp() {
        // 1 tbsp approx 14.79g
        assertEquals(14.7868f, UnitConverter.getWeightInGrams(1f, "tbsp"), 0.01f)
        // 1 tsp approx 4.93g
        assertEquals(4.92892f, UnitConverter.getWeightInGrams(1f, "tsp"), 0.01f)
    }

    @Test
    fun testPieces() {
        // 1 piece with default 150g serving size
        assertEquals(150f, UnitConverter.getWeightInGrams(1f, "piece", 150f), 0.01f)
        // 0.5 piece with default 200g
        assertEquals(100f, UnitConverter.getWeightInGrams(0.5f, "piece", 200f), 0.01f)
    }

    @Test
    fun testUnknownUnit() {
        // Unknown units fallback to default serving size
        assertEquals(100f, UnitConverter.getWeightInGrams(1f, "mystery", 100f), 0.01f)
        assertEquals(200f, UnitConverter.getWeightInGrams(2f, "unknown", 100f), 0.01f)
    }
}
