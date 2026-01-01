package com.example.mediapipe.examples.macrosagent

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mediapipe.examples.macrosagent.domain.FoodParser
import com.example.mediapipe.examples.macrosagent.service.MyFitnessPalAccessibilityService
import com.example.mediapipe.examples.macrosagent.ui.AnalysisScreen
import com.example.mediapipe.examples.macrosagent.ui.CameraScreen
import com.example.mediapipe.examples.macrosagent.ui.MainViewModel
import com.example.mediapipe.examples.macrosagent.ui.theme.MacrosAgentTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }
    
    private val viewModel: MainViewModel by viewModels()
    
    @Inject
    lateinit var foodParser: FoodParser

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
                                viewModel.clearAnalysis()
                            },
                            onAddToMFP = { targetMeal ->
                                startMFPAutomation(targetMeal)
                            }
                        )
                    }
                }
            }
        }
    }

    private fun startMFPAutomation(targetMeal: String) {
        if (!isAccessibilityServiceEnabled()) {
             Toast.makeText(this, "Please enable MacrosAgent Accessibility Service", Toast.LENGTH_LONG).show()
             val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
             startActivity(intent)
             return
        }

        // Parse the result using injected FoodParser
        val foodName = foodParser.parseFoodName(viewModel.analysisResult)
        val calories = foodParser.parseCalories(viewModel.analysisResult)
        val protein = foodParser.parseProtein(viewModel.analysisResult)
        val carbs = foodParser.parseCarbs(viewModel.analysisResult)
        val fat = foodParser.parseFat(viewModel.analysisResult)
        
        // Save to SharedPreferences for the service to pick up
        val prefs = getSharedPreferences("MacrosAgentPrefs", MODE_PRIVATE)
        prefs.edit()
            .putString("LAST_FOOD_SEARCH", foodName)
            .putInt("EXPECTED_CALORIES", calories)
            .putInt("EXPECTED_PROTEIN", protein)
            .putInt("EXPECTED_CARBS", carbs)
            .putInt("EXPECTED_FAT", fat)
            .putString("TARGET_MEAL", targetMeal)
            .apply()

        Log.d(TAG, "🚀 Starting MFP automation: $foodName ($calories cal) for $targetMeal")

        // 🚀 Try deep link first (skips 3-4 navigation steps!)
        val deepLink = Intent(Intent.ACTION_VIEW, Uri.parse("myfitnesspal://food/search"))
        deepLink.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        
        try {
            startActivity(deepLink)
            Toast.makeText(this, "🚀 Searching: $foodName", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "✓ Deep link launched successfully")
        } catch (e: Exception) {
            Log.w(TAG, "⚠️ Deep link failed, falling back to normal launch: ${e.message}")
            // Fallback to normal launch
            val launchIntent = packageManager.getLaunchIntentForPackage("com.myfitnesspal.android")
            if (launchIntent != null) {
                startActivity(launchIntent)
                Toast.makeText(this, "Searching: $foodName", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "MyFitnessPal not installed", Toast.LENGTH_SHORT).show()
            }
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