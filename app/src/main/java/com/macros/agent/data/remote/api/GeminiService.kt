package com.macros.agent.data.remote.api

import android.graphics.Bitmap
import android.util.Log
import com.google.genai.Client
import com.google.genai.types.GenerateContentConfig
import com.google.genai.types.Blob
import com.google.genai.types.Content
import com.google.genai.types.Part
import com.google.gson.Gson
import com.macros.agent.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Data class for a detected food item from Gemini analysis.
 */
data class DetectedFood(
    val name: String,
    val estimatedPortionSize: String,
    val estimatedPortionGrams: Float,
    val calories: Float,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val confidence: Float // 0.0 to 1.0
)

/**
 * Result of Gemini food photo analysis.
 */
data class FoodAnalysisResult(
    val success: Boolean,
    val detectedFoods: List<DetectedFood>,
    val totalCalories: Float,
    val totalProtein: Float,
    val totalCarbs: Float,
    val totalFat: Float,
    val overallConfidence: Float,
    val rawResponse: String,
    val errorMessage: String? = null
)

/**
 * Service for analyzing food photos using Gemini Vision (new google-genai SDK).
 */
@Singleton
class GeminiService @Inject constructor() {
    
    private val gson = Gson()
    
    private val client by lazy {
        try {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isNotBlank()) {
                Log.d("GeminiService", "API Key length: ${apiKey.length}, first 4 chars: ${apiKey.take(4)}...")
            } else {
                Log.e("GeminiService", "GEMINI_API_KEY is blank! Check local.properties")
            }
            
            Client.builder()
                .apiKey(apiKey)
                .build()
        } catch (e: Exception) {
            Log.e("GeminiService", "Failed to initialize Gemini Client", e)
            throw e
        } catch (e: NoClassDefFoundError) {
             Log.e("GeminiService", "Missing dependency for Gemini Client! Ensure org.apache.http.legacy is enabled.", e)
             throw e
        }
    }
    
    private val systemPrompt = """
You are a nutrition analysis API. Your ONLY output must be valid JSON. No explanations, no markdown, no commentary.

TASK: Analyze the food in this image and estimate macros for EACH distinct food item.

OUTPUT FORMAT (strict JSON, nothing else):
{"foods":[{"name":"food name","estimatedPortionSize":"portion","estimatedPortionGrams":100.0,"calories":200.0,"protein":10.0,"carbs":25.0,"fat":8.0,"confidence":0.8}],"overallConfidence":0.8}

RULES:
1. Return ONLY the JSON object. No text before or after.
2. List EACH food item separately (e.g., rice, dal, curry should be 3 separate entries).
3. Use numbers, not strings, for all numeric values.
4. Estimate portion sizes conservatively using USDA values.
5. Confidence should be 0.0-1.0 based on how certain you are.

EXAMPLE for a complex meal (rice + dal + vegetable curry):
{"foods":[{"name":"Basmati Rice","estimatedPortionSize":"1 cup cooked","estimatedPortionGrams":180.0,"calories":210.0,"protein":4.5,"carbs":45.0,"fat":0.5,"confidence":0.85},{"name":"Dal (Lentil Curry)","estimatedPortionSize":"0.75 cup","estimatedPortionGrams":150.0,"calories":180.0,"protein":12.0,"carbs":28.0,"fat":2.0,"confidence":0.75},{"name":"Vegetable Curry","estimatedPortionSize":"0.5 cup","estimatedPortionGrams":100.0,"calories":120.0,"protein":3.0,"carbs":15.0,"fat":6.0,"confidence":0.7}],"overallConfidence":0.77}

If you cannot identify food, return:
{"foods":[],"overallConfidence":0.0,"error":"reason"}
""".trimIndent()

    private fun extractJson(text: String): String? {
        // Try to find JSON block in markdown
        val markdownRegex = "```(?:json)?\\s*([\\s\\S]*?)\\s*```".toRegex()
        val match = markdownRegex.find(text)
        if (match != null) {
            return match.groupValues[1]
        }

        // Fallback: find the first { and last }
        val start = text.indexOf('{')
        val end = text.lastIndexOf('}')
        if (start != -1 && end != -1 && end > start) {
            return text.substring(start, end + 1)
        }

        return null
    }
    
    /**
     * Analyze a food photo and return estimated macros.
     */
    suspend fun analyzeFood(bitmap: Bitmap, userContext: String? = null): FoodAnalysisResult = withContext(Dispatchers.IO) {
        try {
            // Convert bitmap to bytes
            val imageBytes = bitmapToBytes(bitmap)
            
            // Create inline data blob for image
            val imageBlob = Blob.builder()
                .mimeType("image/jpeg")
                .data(imageBytes)
                .build()
            
            // Create parts list with image and text
            val prompt = if (userContext.isNullOrBlank()) {
                systemPrompt
            } else {
                "$systemPrompt\n\nUSER CONTEXT: $userContext\nUse this context (e.g., brand, specific meal details) to improve your estimates."
            }

            val parts = listOf(
                Part.builder().inlineData(imageBlob).build(),
                Part.builder().text(prompt).build()
            )
            
            // Wrap parts in Content
            val content = Content.builder()
                .parts(parts)
                .build()
            
            // Create config with Float values
            val config = GenerateContentConfig.builder()
                .temperature(0.1f)
                .responseMimeType("application/json")
                .topK(32f)
                .topP(0.8f)
                .maxOutputTokens(2048)
                .build()
            
            // Generate content - pass single Content object
            val response = client.models.generateContent(
                "gemini-2.0-flash",
                content,
                config
            )
            
            // Extract text from response using SDK accessors
            // The google-genai SDK 0.x returns a GenerateContentResponse where candidates is a list
            // Extract text from response using SDK accessors
            // The google-genai SDK uses java.util.Optional wrappers
            var responseText = ""
            
            try {
                // Try text() helper method - it returns String? directly
                responseText = response.text() ?: ""
            } catch (e: Exception) {
                // Ignore
            }
            
            if (responseText.isBlank()) {
                try {
                    val candidatesOpt = response.candidates()
                    if (candidatesOpt.isPresent) {
                        val candidatesList = candidatesOpt.get()
                        if (candidatesList.isNotEmpty()) {
                            val firstCandidate = candidatesList[0]
                            val contentOpt = firstCandidate.content()
                            // Try treating content as Optional
                            if (contentOpt.isPresent) {
                                val content = contentOpt.get()
                                val partsOpt = content.parts()
                                if (partsOpt.isPresent) {
                                    val partsList = partsOpt.get()
                                    if (partsList.isNotEmpty()) {
                                        // text() on Part returns Optional<String>
                                        val partTextOpt = partsList[0].text()
                                        if (partTextOpt.isPresent) {
                                            responseText = partTextOpt.get()
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.w("GeminiService", "Failed to extract text from response", e)
                }
            }
            
            Log.d("GeminiService", "Response: ${responseText.take(200)}...")
            parseResponse(responseText)
        } catch (e: Exception) {
            val msg = e.message ?: "Unknown error"
            Log.e("GeminiService", "Error analyzing food", e)
            
            val friendlyMsg = when {
                msg.contains("403") || msg.contains("PERMISSION_DENIED") -> 
                    "API Key Error: Please verify your Gemini API key is valid and has access to gemini-2.5-flash."
                msg.contains("API key") -> 
                    "API Key Error: $msg"
                else -> 
                    "Analysis failed: $msg"
            }
            
            FoodAnalysisResult(
                success = false,
                detectedFoods = emptyList(),
                totalCalories = 0f,
                totalProtein = 0f,
                totalCarbs = 0f,
                totalFat = 0f,
                overallConfidence = 0f,
                rawResponse = "",
                errorMessage = friendlyMsg
            )
        }
    }
    
    private fun bitmapToBytes(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        return outputStream.toByteArray()
    }
    
    private fun parseResponse(responseText: String): FoodAnalysisResult {
        return try {
            val jsonStr = extractJson(responseText)
            
            if (jsonStr == null) {
                return FoodAnalysisResult(
                    success = false,
                    detectedFoods = emptyList(),
                    totalCalories = 0f,
                    totalProtein = 0f,
                    totalCarbs = 0f,
                    totalFat = 0f,
                    overallConfidence = 0f,
                    rawResponse = responseText,
                    errorMessage = "Could not parse JSON from response"
                )
            }
            
            val parsed = gson.fromJson(jsonStr, GeminiFoodResponse::class.java)
            
            val foods = parsed.foods.map { food ->
                DetectedFood(
                    name = food.name,
                    estimatedPortionSize = food.estimatedPortionSize,
                    estimatedPortionGrams = food.estimatedPortionGrams,
                    calories = food.calories,
                    protein = food.protein,
                    carbs = food.carbs,
                    fat = food.fat,
                    confidence = food.confidence
                )
            }
            
            FoodAnalysisResult(
                success = foods.isNotEmpty(),
                detectedFoods = foods,
                totalCalories = foods.sumOf { it.calories.toDouble() }.toFloat(),
                totalProtein = foods.sumOf { it.protein.toDouble() }.toFloat(),
                totalCarbs = foods.sumOf { it.carbs.toDouble() }.toFloat(),
                totalFat = foods.sumOf { it.fat.toDouble() }.toFloat(),
                overallConfidence = parsed.overallConfidence,
                rawResponse = responseText,
                errorMessage = parsed.error
            )
        } catch (e: Exception) {
            FoodAnalysisResult(
                success = false,
                detectedFoods = emptyList(),
                totalCalories = 0f,
                totalProtein = 0f,
                totalCarbs = 0f,
                totalFat = 0f,
                overallConfidence = 0f,
                rawResponse = responseText,
                errorMessage = "Failed to parse response: ${e.message}"
            )
        }
    }

    /**
     * Adjust macros for a specific food based on a natural language description (e.g., "2 pieces", "half a cup").
     */
    suspend fun adjustMacros(
        foodName: String,
        originalMacros: DetectedFood,
        adjustmentRequest: String
    ): DetectedFood = withContext(Dispatchers.IO) {
        val adjustmentPrompt = """
            You are a nutrition expert. Adjust the macros for the following food based on the user's request.
            
            FOOD: $foodName
            ORIGINAL PORTION: ${originalMacros.estimatedPortionSize} (${originalMacros.estimatedPortionGrams}g)
            ORIGINAL MACROS: ${originalMacros.calories} cal, ${originalMacros.protein}g protein, ${originalMacros.carbs}g carbs, ${originalMacros.fat}g fat
            
            USER REQUEST: $adjustmentRequest
            
            Respond ONLY with valid JSON in this exact format, no additional text:
            {
              "name": "$foodName",
              "estimatedPortionSize": "new portion description",
              "estimatedPortionGrams": 0.0,
              "calories": 0.0,
              "protein": 0.0,
              "carbs": 0.0,
              "fat": 0.0,
              "confidence": 0.9
            }
        """.trimIndent()

        try {
            val content = Content.builder()
                .parts(listOf(Part.builder().text(adjustmentPrompt).build()))
                .build()
            
            val config = GenerateContentConfig.builder()
                .temperature(0.1f)
                .build()
            
            val response = client.models.generateContent("gemini-2.0-flash", content, config)
            val responseText = response.text() ?: ""
            
            val jsonStr = extractJson(responseText)
            if (jsonStr == null) {
                Log.e("GeminiService", "Could not extract JSON from adjustMacros response")
                return@withContext originalMacros
            }
            
            val adjusted = gson.fromJson(jsonStr, DetectedFood::class.java)
            adjusted
        } catch (e: Exception) {
            Log.e("GeminiService", "Error adjusting macros", e)
            originalMacros // Fallback to original
        }
    }
}

// Internal DTOs for parsing Gemini response
private data class GeminiFood(
    val name: String = "",
    val estimatedPortionSize: String = "",
    val estimatedPortionGrams: Float = 0f,
    val calories: Float = 0f,
    val protein: Float = 0f,
    val carbs: Float = 0f,
    val fat: Float = 0f,
    val confidence: Float = 0f
)

private data class GeminiFoodResponse(
    val foods: List<GeminiFood> = emptyList(),
    val overallConfidence: Float = 0f,
    val error: String? = null
)
