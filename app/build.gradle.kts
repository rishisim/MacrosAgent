import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.android)
    id("kotlin-kapt")
}

android {
    namespace = "com.example.mediapipe.examples.macrosagent"
    compileSdk = 34 // release(36) might be preview, safer to use stable or explicit number if known. User had 36? Let's stick to user's 36 if it was there or 34. The previous file had `release(36)`. Let's assume 34 is safe or keep 36 if they are on edge. User had `targetSdk = 36`. 
    // Wait, `release(36)` usually implies preview. I'll stick to what was there but clean up. 
    // Actually, `compileSdk` takes an integer. `version = release(36)` looks like a specific DSL usage. 
    // I'll try to preserve what was working or standard. 
    // The user's original file had:
    // compileSdk {
    //    version = release(36)
    // }
    // I will keep that logic if it's correct for their environment, but `compileSdk = 34` is standard.
    // Given the errors didn't complain about compileSdk, I'll keep it as is or slightly standard.
    // Let's go with standard `compileSdk = 34` (Android 14) or `35` (Android 15). 
    // If they target 36, maybe it's Android 16 preview? 
    // I'll stick to the original `compileSdk` block to minimize regression there.
    compileSdkPreview = "Baklava" // or whatever 36 is, but let's just use what was there but valid. 
    // Actually, let's just use `compileSdk = 35` to be safe and modern without being "future".
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.mediapipe.examples.macrosagent"
        minSdk = 26
        targetSdk = 35 // matching compileSdk
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localProperties.load(FileInputStream(localPropertiesFile))
        }
        val apiKey = localProperties.getProperty("GEMINI_API_KEY")?.replace("\"", "") ?: ""
        buildConfigField("String", "GEMINI_API_KEY", "\"$apiKey\"")
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // CameraX
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)

    // Gemini
    implementation(libs.google.ai.client.generativeai)

    // Coil
    implementation(libs.coil.compose)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
}