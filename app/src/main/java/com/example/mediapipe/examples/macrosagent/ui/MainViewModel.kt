package com.example.mediapipe.examples.macrosagent.ui

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediapipe.examples.macrosagent.data.GeminiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: GeminiRepository
) : ViewModel() {
    
    var capturedBitmap by mutableStateOf<Bitmap?>(null)
    var analysisResult by mutableStateOf("")
    var isLoading by mutableStateOf(false)

    fun analyze(bitmap: Bitmap) {
        capturedBitmap = bitmap
        isLoading = true
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                repository.analyzeImage(bitmap)
            }
            analysisResult = result
            isLoading = false
        }
    }

    fun loadMockData() {
        // Create a black placeholder bitmap
        capturedBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        
        // Mock analysis result
        analysisResult = """
            {
                "items": [
                    {
                        "name": "Clementine (Peeled)",
                        "calories": 35,
                        "protein_g": 1,
                        "carbs_g": 9,
                        "fat_g": 0
                    }
                ],
                "total": {
                    "calories": 35,
                    "protein_g": 1,
                    "carbs_g": 9,
                    "fat_g": 0
                },
                "summary": "Detected a peeled clementine (~74g). A healthy snack!"
            }
        """.trimIndent()
        isLoading = false
    }
    
    fun clearAnalysis() {
        analysisResult = ""
    }
}
