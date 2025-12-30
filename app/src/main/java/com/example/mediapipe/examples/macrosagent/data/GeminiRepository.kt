package com.example.mediapipe.examples.macrosagent.data

import android.graphics.Bitmap
import com.google.ai.client.generativeai.GenerativeModel
import com.example.mediapipe.examples.macrosagent.BuildConfig

class GeminiRepository {
    private val apiKey = BuildConfig.GEMINI_API_KEY 
    
    private val generativeModel by lazy {
        GenerativeModel(
            modelName = "gemini-2.5-flash",
            apiKey = apiKey
        )
    }

    suspend fun analyzeImage(bitmap: Bitmap): String {
        val prompt = """
            You are an expert nutritionist and food analyst.
            1. Analyze the provided food image to identify all food items.
            2. Estimate precise portion sizes and calculate macronutrients (Calories, Protein, Carbs, Fat) for each item and the total meal.
            3. Return the result strictly in JSON format with the schema:
               {
                 "items": [{"name": "Steak", "calories": 500, "protein_g": 45, "carbs_g": 0, "fat_g": 35}],
                 "total": {"calories": 500, "protein_g": 45, "carbs_g": 0, "fat_g": 35},
                 "summary": "Grilled ribeye steak (approx 8oz)"
               }
            4. Be concise and accurate. Do not include markdown code blocks, just the raw JSON.
        """.trimIndent()

        return try {
            val response = generativeModel.generateContent(
                com.google.ai.client.generativeai.type.content {
                    image(bitmap)
                    text(prompt)
                }
            )
            response.text ?: "{}"
        } catch (e: Exception) {
            e.printStackTrace()
            "{ \"error\": \"${e.message}\" }"
        }
    }
}
