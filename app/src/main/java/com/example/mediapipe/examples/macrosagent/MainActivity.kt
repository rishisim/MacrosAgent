package com.example.mediapipe.examples.macrosagent

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.Settings
import android.content.ComponentName
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mediapipe.examples.macrosagent.data.GeminiRepository
import com.example.mediapipe.examples.macrosagent.service.MyFitnessPalAccessibilityService
import com.example.mediapipe.examples.macrosagent.ui.AnalysisScreen
import com.example.mediapipe.examples.macrosagent.ui.CameraScreen
import com.example.mediapipe.examples.macrosagent.ui.theme.MacrosAgentTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel : ViewModel() {
    private val repository = GeminiRepository()
    
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
}

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (!isGranted) {
                Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        setContent {
            MacrosAgentTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "camera") {
                    composable("camera") {
                        CameraScreen(
                            onImageCaptured = { bitmap ->
                                viewModel.analyze(bitmap)
                                navController.navigate("analysis")
                            },
                            onMockCaptured = {
                                viewModel.loadMockData()
                                navController.navigate("analysis")
                            }
                        )
                    }
                    composable("analysis") {
                        AnalysisScreen(
                            bitmap = viewModel.capturedBitmap,
                            analysisResult = viewModel.analysisResult,
                            onRetake = {
                                navController.popBackStack()
                                viewModel.analysisResult = ""
                            },
                            onAddToMFP = {
                                startMFPAutomation()
                            }
                        )
                    }
                }
            }
        }
    }

    private fun startMFPAutomation() {
        if (!isAccessibilityServiceEnabled()) {
             Toast.makeText(this, "Please enable MacrosAgent Accessibility Service", Toast.LENGTH_LONG).show()
             val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
             startActivity(intent)
             return
        }

        // Parse the result to get a friendly food name
        val foodName = parseFoodName(viewModel.analysisResult)
        
        // Save to SharedPreferences for the service to pick up
        val prefs = getSharedPreferences("MacrosAgentPrefs", MODE_PRIVATE)
        prefs.edit().putString("LAST_FOOD_SEARCH", foodName).apply()

        Toast.makeText(this, "Searching for: $foodName", Toast.LENGTH_SHORT).show()

        val launchIntent = packageManager.getLaunchIntentForPackage("com.myfitnesspal.android")
        if (launchIntent != null) {
            startActivity(launchIntent)
        } else {
            Toast.makeText(this, "MyFitnessPal not installed", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun parseFoodName(json: String): String {
        return try {
            val jsonObject = org.json.JSONObject(json)
            val items = jsonObject.getJSONArray("items")
            if (items.length() > 0) {
                items.getJSONObject(0).getString("name")
            } else {
                "Food"
            }
        } catch (e: Exception) {
            "Food"
        }
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val expectedComponentName = ComponentName(this, MyFitnessPalAccessibilityService::class.java)
        
        val enabledServicesSetting = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false

        val stringColonSplitter = TextUtils.SimpleStringSplitter(':')
        stringColonSplitter.setString(enabledServicesSetting)

        while (stringColonSplitter.hasNext()) {
            val componentNameString = stringColonSplitter.next()
            val enabledComponent = ComponentName.unflattenFromString(componentNameString)
            if (enabledComponent != null && enabledComponent == expectedComponentName) {
                return true
            }
        }
        return false
    }
}