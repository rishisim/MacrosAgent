package com.example.mediapipe.examples.macrosagent.service

import android.graphics.Bitmap
import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.example.mediapipe.examples.macrosagent.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

/**
 * Vision-based fallback for finding UI elements when accessibility tree fails.
 * This is the "Eyes" of the Cyborg Agent - uses Gemini to locate elements by screenshot.
 */
class VisionAgent {
    
    companion object {
        private const val TAG = "VisionAgent"
    }
    
    private val generativeModel by lazy {
        GenerativeModel(
            modelName = "gemini-2.5-flash",
            apiKey = BuildConfig.GEMINI_API_KEY
        )
    }
    
    /**
     * Find a UI element by taking a screenshot and asking Gemini for coordinates.
     * 
     * @param screenshot The current screen bitmap
     * @param targetDescription Description of what to find (e.g., "Search button", "Close popup X")
     * @return Pair of (x, y) coordinates if found, null otherwise
     */
    suspend fun findElementByVision(
        screenshot: Bitmap,
        targetDescription: String
    ): Pair<Int, Int>? = withContext(Dispatchers.IO) {
        try {
            val prompt = """
                You are a UI automation assistant analyzing a mobile app screenshot.
                
                TASK: Find the UI element matching: "$targetDescription"
                
                INSTRUCTIONS:
                1. Look for buttons, text, icons, or any tappable element matching the description
                2. If found, return the CENTER coordinates of that element USING NORMALIZED COORDINATES (0-1000 scale).
                   (0,0) is top-left, (1000,1000) is bottom-right.
                3. Return ONLY valid JSON in this format: {"x": 123, "y": 456}
                4. If not found, return: {"error": "not_found"}
                
                IMPORTANT: Do not include any explanation or markdown outside the JSON block.
            """.trimIndent()
            
            val response = generativeModel.generateContent(
                content {
                    image(screenshot)
                    text(prompt)
                }
            )
            
            val responseText = response.text?.trim() ?: return@withContext null
            Log.d(TAG, "Vision response: $responseText")
            
            parseCoordinates(responseText)
        } catch (e: Exception) {
            Log.e(TAG, "Vision fallback failed: ${e.message}")
            null
        }
    }
    
    /**
     * Ask Gemini to identify what screen we're looking at.
     */
    suspend fun classifyScreenByVision(screenshot: Bitmap): ScreenType = withContext(Dispatchers.IO) {
        try {
            val prompt = """
                You are analyzing a MyFitnessPal mobile app screenshot.
                
                Classify this screen as ONE of these types:
                - HOME_SCREEN: Main dashboard with "Diary", "Newsfeed", calorie circles
                - SEARCH_SCREEN: Food search bar visible, keyboard may be showing
                - POPUP_DIALOG: Modal popup (Premium offer, Rate Us, notifications)
                - MEAL_SELECTION: Breakfast/Lunch/Dinner/Snacks selection
                - DIARY_SCREEN: Food diary showing logged meals
                - UNKNOWN: Cannot determine
                
                Return ONLY the type name, nothing else.
            """.trimIndent()
            
            val response = generativeModel.generateContent(
                content {
                    image(screenshot)
                    text(prompt)
                }
            )
            
            val responseText = response.text?.trim()?.uppercase() ?: "UNKNOWN"
            Log.d(TAG, "Screen classification: $responseText")
            
            try {
                ScreenType.valueOf(responseText)
            } catch (e: IllegalArgumentException) {
                ScreenType.UNKNOWN
            }
        } catch (e: Exception) {
            Log.e(TAG, "Screen classification failed: ${e.message}")
            ScreenType.UNKNOWN
        }
    }
    
    /**
     * Find the best element to close a popup (X button, Close, Not Now, etc.)
     */
    suspend fun findPopupDismissButton(screenshot: Bitmap): Pair<Int, Int>? {
        return findElementByVision(
            screenshot,
            "X button, Close button, 'Not Now', 'Maybe Later', or 'Skip' - whichever is visible to dismiss this popup"
        )
    }
    
    private fun extractJson(text: String): String {
        // Simpler regex to find the first '{' and last '}'
        val start = text.indexOf('{')
        val end = text.lastIndexOf('}')
        return if (start != -1 && end != -1 && end > start) {
            text.substring(start, end + 1)
        } else {
            text.trim()
        }
    }

    private fun parseCoordinates(json: String): Pair<Int, Int>? {
        return try {
            val cleanJson = extractJson(json)
            val jsonObject = JSONObject(cleanJson)
            
            if (jsonObject.has("error")) {
                null
            } else {
                val x = jsonObject.getInt("x")
                val y = jsonObject.getInt("y")
                Pair(x, y)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse coordinates from: $json. Error: ${e.message}")
            null
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // SEARCH RESULTS EVALUATION
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * Decision result for search evaluation.
     */
    data class SearchResultDecision(
        val action: String,   // SELECT_FOOD, RETRY_SEARCH, USE_SUGGESTION  
        val target: String?,  // Better query or suggestion text if retrying
        val reason: String
    )
    
    /**
     * Evaluate search results and decide what to do next.
     */
    suspend fun evaluateSearchResults(
        screenshot: Bitmap,
        originalSearch: String,
        expectedCalories: Int? = null
    ): SearchResultDecision = withContext(Dispatchers.IO) {
        try {
            val calorieHint = if (expectedCalories != null) {
                "Expected calories for this food: ~$expectedCalories cal."
            } else ""
            
            val prompt = """
                You are analyzing MyFitnessPal search results for: "$originalSearch"
                $calorieHint
                
                ANALYZE THE SCREEN:
                1. Are there food results showing? (food items with calories listed)
                2. Is the screen mostly empty with "Suggested Searches" at bottom?
                3. Are there relevant matches for "$originalSearch"?
                
                DECIDE WHAT TO DO:
                - SELECT_FOOD: Good results found, proceed to select one
                - RETRY_SEARCH: No results or poor matches, suggest a simpler/better search term
                - USE_SUGGESTION: No results, but there's a good "Suggested Search" pill/button visible
                
                RESPONSE FORMAT (JSON ONLY):
                {"action": "SELECT_FOOD", "target": null, "reason": "Found multiple matches for clementine"}
            """.trimIndent()
            
            val response = generativeModel.generateContent(
                content {
                    image(screenshot)
                    text(prompt)
                }
            )
            
            val responseText = response.text?.trim() ?: return@withContext SearchResultDecision(
                "SELECT_FOOD", null, "No response from vision model"
            )
            Log.d(TAG, "Search evaluation: $responseText")
            
            parseSearchDecision(responseText)
        } catch (e: Exception) {
            Log.e(TAG, "Search evaluation failed: ${e.message}")
            SearchResultDecision("SELECT_FOOD", null, "Error: ${e.message}")
        }
    }
    
    private fun parseSearchDecision(json: String): SearchResultDecision {
        return try {
            val cleanJson = extractJson(json)
            val obj = JSONObject(cleanJson)
            SearchResultDecision(
                action = obj.optString("action", "SELECT_FOOD"),
                target = obj.optString("target", null).takeIf { it.isNotEmpty() && it != "null" },
                reason = obj.optString("reason", "")
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse search decision from: $json. Error: ${e.message}")
            SearchResultDecision("SELECT_FOOD", null, "Parse error, proceeding")
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // FOOD SELECTION
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * Decision result for food selection.
     */
    data class FoodSelectionDecision(
        val action: String,         // QUICK_ADD, VIEW_DETAILS
        val coordinates: Pair<Int, Int>?,
        val foodName: String?,
        val reason: String
    )
    
    /**
     * Select the best food item from search results.
     */
    suspend fun selectBestFood(
        screenshot: Bitmap,
        targetFood: String,
        expectedCalories: Int? = null,
        expectedProtein: Int? = null,
        expectedCarbs: Int? = null,
        expectedFat: Int? = null
    ): FoodSelectionDecision = withContext(Dispatchers.IO) {
        try {
            val calorieHint = if (expectedCalories != null) {
                "Target: ~$expectedCalories cal, ${expectedProtein}g Protein, ${expectedCarbs}g Carbs, ${expectedFat}g Fat."
            } else ""
            
            val prompt = """
                You are selecting a food item from MyFitnessPal search results.
                Looking for: "$targetFood"
                $calorieHint
                
                TASK: Find the BEST matching food item on screen based on name AND macros.
                
                CRITERIA:
                1. Name Match: Must be similar to "$targetFood".
                2. Macro Match: Prioritize items that match the Protein/Carb/Fat targets if provided.
                   - Example: If target is "Chicken Breast (30g Protein)", pick the entry with ~30g protein, not the one with 10g.
                
                DECIDE:
                - QUICK_ADD: The serving size/quantity looks exactly right, click the PLUS (+) button directly. Only use this if you see a "+" button next to the food.
                - VIEW_DETAILS: Use this to adjust serving size, or if there is no "+" button. Click on the food name/text area.
                
                INSTRUCTIONS:
                1. Return the CENTER coordinates of the element you want to click.
                2. Use NORMALIZED COORDINATES (0-1000).
                3. Return ONLY valid JSON in this format:
                   {"action": "QUICK_ADD", "x": 920, "y": 450, "food_name": "Peeled Clementines", "reason": "Found exact match with + button and correct macros"}
            """.trimIndent()
            
            val response = generativeModel.generateContent(
                content {
                    image(screenshot)
                    text(prompt)
                }
            )
            
            val responseText = response.text?.trim() ?: return@withContext FoodSelectionDecision(
                "VIEW_DETAILS", null, null, "No response"
            )
            Log.d(TAG, "Food selection: $responseText")
            
            parseFoodSelection(responseText)
        } catch (e: Exception) {
            Log.e(TAG, "Food selection failed: ${e.message}")
            FoodSelectionDecision("VIEW_DETAILS", null, null, "Error: ${e.message}")
        }
    }
    
    private fun parseFoodSelection(json: String): FoodSelectionDecision {
        return try {
            val cleanJson = extractJson(json)
            val obj = JSONObject(cleanJson)
            
            val coords = if (obj.has("x") && obj.has("y")) {
                Pair(obj.getInt("x"), obj.getInt("y"))
            } else null
            
            FoodSelectionDecision(
                action = obj.optString("action", "VIEW_DETAILS"),
                coordinates = coords,
                foodName = if (obj.isNull("food_name")) null else obj.optString("food_name").takeIf { it != "null" && it.isNotEmpty() },
                reason = obj.optString("reason", "")
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse food selection from: $json. Error: ${e.message}")
            FoodSelectionDecision("VIEW_DETAILS", null, null, "Parse error")
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // SERVING SIZE ADJUSTMENT
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * Decision for serving size screen.
     */
    data class ServingDecision(
        val action: String,  // CONFIRM, ADJUST_SERVINGS, CHANGE_UNIT
        val coordinates: Pair<Int, Int>?,
        val newValue: String?,
        val reason: String
    )
    
    /**
     * Analyze food detail/serving size screen and decide what to do.
     */
    suspend fun analyzeServingScreen(
        screenshot: Bitmap,
        targetFood: String,
        expectedCalories: Int? = null,
        expectedProtein: Int? = null,
        expectedCarbs: Int? = null,
        expectedFat: Int? = null
    ): ServingDecision = withContext(Dispatchers.IO) {
        try {
            val calorieHint = if (expectedCalories != null) {
                "Target: ~$expectedCalories cal (P:$expectedProtein C:$expectedCarbs F:$expectedFat). Adjust serving to match."
            } else "Just confirm with current serving size."
            
            val prompt = """
                You are on MyFitnessPal's food detail/serving size screen.
                Food: "$targetFood"
                $calorieHint
                
                ANALYZE: 
                - What's the current serving size shown?
                - What are the calories for this serving?
                - Is there a checkmark/confirm button (usually top-right or a "checkmark" icon) to add this food?
                
                DECIDE:
                - CONFIRM: Serving looks good, click the checkmark/add button
                - ADJUST_SERVINGS: Need to change the number of servings
                - CHANGE_UNIT: Need to change serving unit (g, oz, cup, etc.)
                
                EXAMPLE RESPONSE:
                {"action": "CONFIRM", "x": 950, "y": 80, "new_value": null, "reason": "Calories match expected value, clicking checkmark"}
                
                Return ONLY valid JSON.
            """.trimIndent()
            
            val response = generativeModel.generateContent(
                content {
                    image(screenshot)
                    text(prompt)
                }
            )
            
            val responseText = response.text?.trim() ?: return@withContext ServingDecision(
                "CONFIRM", null, null, "No response"
            )
            Log.d(TAG, "Serving decision: $responseText")
            
            parseServingDecision(responseText)
        } catch (e: Exception) {
            Log.e(TAG, "Serving analysis failed: ${e.message}")
            ServingDecision("CONFIRM", null, null, "Error, trying to confirm")
        }
    }
    
    private fun parseServingDecision(json: String): ServingDecision {
        return try {
            val cleanJson = extractJson(json)
            val obj = JSONObject(cleanJson)
            
            val coords = if (obj.has("x") && obj.has("y")) {
                Pair(obj.getInt("x"), obj.getInt("y"))
            } else null
            
            ServingDecision(
                action = obj.optString("action", "CONFIRM"),
                coordinates = coords,
                newValue = if (obj.isNull("new_value")) null else obj.optString("new_value").takeIf { it != "null" && it.isNotEmpty() },
                reason = obj.optString("reason", "")
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse serving decision from: $json. Error: ${e.message}")
            ServingDecision("CONFIRM", null, null, "Parse error")
        }
    }
}

