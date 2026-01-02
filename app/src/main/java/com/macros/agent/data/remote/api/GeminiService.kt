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
You are a nutrition analysis expert. Analyze the food in this image and provide detailed macro estimates.

For each distinct food item you see, estimate:
1. What the food is
2. Approximate portion size (e.g., "1 cup", "6 oz", "medium slice")
3. Estimated weight in grams
4. Calories, protein, carbs, and fat

Respond ONLY with valid JSON in this exact format, no additional text:
{
  "foods": [
    {
      "name": "food name",
      "estimatedPortionSize": "portion description",
      "estimatedPortionGrams": 150.0,
      "calories": 250.0,
      "protein": 20.0,
      "carbs": 30.0,
      "fat": 8.0,
      "confidence": 0.85
    }
  ],
  "overallConfidence": 0.8
}

If you cannot identify food in the image, respond with:
{
  "foods": [],
  "overallConfidence": 0.0,
  "error": "description of why analysis failed"
}

Be conservative with estimates. Use typical USDA values as reference.
""".trimIndent()
    
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
                .temperature(0.3f)
                .topK(32f)
                .topP(0.8f)
                .maxOutputTokens(2048)
                .build()
            
            // Generate content - pass single Content object
            val response = client.models.generateContent(
                "gemini-2.5-flash",
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
            // Extract JSON from response (in case there's surrounding text)
            val jsonStart = responseText.indexOf('{')
            val jsonEnd = responseText.lastIndexOf('}') + 1
            
            if (jsonStart == -1 || jsonEnd <= jsonStart) {
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
            
            val jsonStr = responseText.substring(jsonStart, jsonEnd)
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
