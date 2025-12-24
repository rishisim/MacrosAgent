package com.example.mediapipe.examples.macrosagent.service

import android.graphics.Rect
import android.view.accessibility.AccessibilityNodeInfo

/**
 * Represents an element on screen that can be clicked.
 */
data class ClickableElement(
    val text: String?,
    val contentDescription: String?,
    val bounds: Rect?,
    val nodeInfo: AccessibilityNodeInfo
) {
    fun matchesAny(terms: List<String>): Boolean {
        val searchableText = listOfNotNull(text, contentDescription)
            .joinToString(" ")
            .lowercase()
        return terms.any { searchableText.contains(it.lowercase()) }
    }
}

/**
 * Represents an editable text field on screen.
 */
data class EditableField(
    val hint: String?,
    val currentText: String?,
    val bounds: Rect?,
    val nodeInfo: AccessibilityNodeInfo
)

/**
 * Complete snapshot of what the agent "sees" on the current screen.
 */
data class ScreenState(
    val visibleTexts: List<String>,
    val clickableElements: List<ClickableElement>,
    val editableFields: List<EditableField>,
    val packageName: String?
) {
    /**
     * Get all text content as a single lowercase string for easy searching.
     */
    fun getAllTextLowercase(): String = visibleTexts.joinToString(" ").lowercase()
    
    /**
     * Check if any visible text contains any of the given terms.
     */
    fun containsAnyText(vararg terms: String): Boolean {
        val allText = getAllTextLowercase()
        return terms.any { allText.contains(it.lowercase()) }
    }
    
    /**
     * Find clickable elements matching any of the given terms.
     */
    fun findClickables(vararg terms: List<String>): List<ClickableElement> {
        return clickableElements.filter { element ->
            terms.any { termList -> element.matchesAny(termList) }
        }
    }
}

/**
 * Types of screens the agent can recognize in MyFitnessPal.
 */
enum class ScreenType {
    /** Main MFP home - sees "Diary", "Newsfeed", "Calories remaining" */
    HOME_SCREEN,
    
    /** Search bar is visible and ready for input */
    SEARCH_SCREEN,
    
    /** Modal popup - Premium offers, Rate Us, notifications, etc. */
    POPUP_DIALOG,
    
    /** Meal selection screen - Breakfast/Lunch/Dinner/Snacks */
    MEAL_SELECTION,
    
    /** Food diary view showing logged meals */
    DIARY_SCREEN,
    
    /** Search results with food items displayed */
    SEARCH_RESULTS_GOOD,
    
    /** Empty/no search results, suggested searches shown */
    SEARCH_RESULTS_EMPTY,
    
    /** Individual food detail page (nutrition info, serving options) */
    FOOD_DETAIL,
    
    /** Serving size adjustment screen */
    SERVING_SIZE,
    
    /** Unknown screen - trigger vision fallback */
    UNKNOWN
}

/**
 * Result of screen classification with confidence info.
 */
data class ScreenClassification(
    val type: ScreenType,
    val confidence: Float,
    val detectedFeatures: List<String>
)
