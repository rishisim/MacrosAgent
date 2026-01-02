package com.macros.agent.ui.screens.photo

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.macros.agent.data.local.entity.DiaryEntry
import com.macros.agent.data.local.entity.MealType
import com.macros.agent.data.remote.api.DetectedFood
import com.macros.agent.ui.theme.CaloriesColor
import com.macros.agent.ui.theme.CarbsColor
import com.macros.agent.ui.theme.FatColor
import com.macros.agent.ui.theme.ProteinColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoScreen(
    onAnalysisComplete: (List<DiaryEntry>) -> Unit,
    viewModel: PhotoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Gallery picker
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                val bitmap = loadBitmapFromUri(context, it)
                if (bitmap != null) {
                    viewModel.analyzePhoto(bitmap, it.toString())
                }
            }
        }
    }
    
    // Camera permission
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        )
    }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }
    
    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    if (uiState.isAddingToLog) {
        // Navigate away when done
        LaunchedEffect(Unit) {
            onAnalysisComplete(emptyList()) // We don't really need to pass entries back, navigation handles it
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Food Snap") },
                actions = {
                    if (uiState.analysisState is AnalysisState.Success) {
                        IconButton(onClick = { viewModel.reset() }) {
                            Icon(Icons.Default.Refresh, "Reset")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = uiState.analysisState) {
                is AnalysisState.Idle, is AnalysisState.Capturing -> {
                    if (hasCameraPermission) {
                        CameraCaptureView(
                            onImageCaptured = { bitmap ->
                                viewModel.analyzePhoto(bitmap)
                            },
                            onGalleryClick = {
                                galleryLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            userContext = uiState.userContext,
                            onContextChange = { viewModel.updateUserContext(it) }
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Camera permission required")
                        }
                    }
                }
                
                is AnalysisState.Analyzing -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Gemini is analyzing your food...")
                        }
                    }
                }
                
                is AnalysisState.Success -> {
                    AnalysisResultView(
                        detectedFoods = uiState.detectedFoods,
                        onAdd = { viewModel.addToLog() },
                        onRemove = { index -> viewModel.removeDetectedFood(index) },
                        onUpdate = { index, food -> viewModel.updateDetectedFood(index, food) },
                        selectedMealType = uiState.selectedMealType,
                        onMealTypeSelected = { viewModel.selectMealType(it) }
                    )
                }
                
                is AnalysisState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Analysis Failed",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(state.message) // Error message
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.reset() }) {
                                Text("Try Again")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CameraCaptureView(
    onImageCaptured: (Bitmap) -> Unit,
    onGalleryClick: () -> Unit,
    userContext: String,
    onContextChange: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val imageCapture = remember { ImageCapture.Builder().build() }
    
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
                
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build()
                    preview.setSurfaceProvider(previewView.surfaceProvider)
                    
                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            imageCapture
                        )
                    } catch (e: Exception) {
                        Log.e("Camera", "Use case binding failed", e)
                    }
                }, ContextCompat.getMainExecutor(ctx))
                
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )
        
        // Context Input at the top
        Card(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.5f))
        ) {
            OutlinedTextField(
                value = userContext,
                onValueChange = onContextChange,
                placeholder = { Text("Add context (e.g. brand, 'large bowl')", color = Color.LightGray) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
                colors = androidx.compose.material3.TextFieldDefaults.colors(
                    focusedLabelColor = Color.White,
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent
                )
            )
        }
        
        // Controls
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(32.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onGalleryClick,
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(Icons.Default.PhotoLibrary, "Gallery", tint = Color.White)
            }
            
            FloatingActionButton(
                onClick = {
                    val cameraExecutor = Executors.newSingleThreadExecutor()
                    imageCapture.takePicture(cameraExecutor, object : ImageCapture.OnImageCapturedCallback() {
                        override fun onCaptureSuccess(image: ImageProxy) {
                            try {
                                val bitmap = image.toBitmap()
                                image.close() // Close the proxy immediately after conversion
                                
                                // Return to main thread to update UI
                                val mainExecutor = ContextCompat.getMainExecutor(context)
                                mainExecutor.execute {
                                    onImageCaptured(bitmap)
                                }
                            } catch (e: Exception) {
                                Log.e("Camera", "Error processing captured image", e)
                                image.close()
                            } finally {
                                cameraExecutor.shutdown()
                            }
                        }
                        
                        override fun onError(exception: ImageCaptureException) {
                            Log.e("Camera", "Photo capture failed: ${exception.message}", exception)
                            cameraExecutor.shutdown()
                        }
                    })
                },
                modifier = Modifier.size(72.dp),
                containerColor = Color.White
            ) {
                Icon(Icons.Default.Camera, "Take Photo", modifier = Modifier.size(32.dp), tint = Color.Black)
            }
            
            Spacer(modifier = Modifier.size(48.dp)) // Contrast balance
        }
    }
}

@Composable
fun AnalysisResultView(
    detectedFoods: List<DetectedFood>,
    onAdd: () -> Unit,
    onRemove: (Int) -> Unit,
    onUpdate: (Int, DetectedFood) -> Unit,
    selectedMealType: MealType,
    onMealTypeSelected: (MealType) -> Unit
) {
    if (detectedFoods.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No foods detected. Try another photo.")
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                "Detected Foods",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Meal Type Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MealType.entries.forEach { mealType ->
                    OutlinedButton(
                        onClick = { onMealTypeSelected(mealType) },
                        colors = if (selectedMealType == mealType) {
                            androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        } else {
                            androidx.compose.material3.ButtonDefaults.outlinedButtonColors()
                        }
                    ) {
                        Text(mealType.displayName)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(detectedFoods) { index, food ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = food.name,
                                    onValueChange = { onUpdate(index, food.copy(name = it)) },
                                    modifier = Modifier.weight(1f),
                                    textStyle = MaterialTheme.typography.titleMedium,
                                    label = { Text("Food Name") }
                                )
                                IconButton(onClick = { onRemove(index) }) {
                                    Icon(Icons.Default.Delete, "Remove")
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = food.estimatedPortionGrams.toInt().toString(),
                                    onValueChange = { 
                                        it.toFloatOrNull()?.let { grams -> 
                                            // Recalculate macros based on new grams if possible?
                                            // Gemini gives us the absolute values. 
                                            // For simplicity, let's just update grams.
                                            // Real app would scale others too.
                                            val scale = grams / food.estimatedPortionGrams
                                            onUpdate(index, food.copy(
                                                estimatedPortionGrams = grams,
                                                calories = food.calories * scale,
                                                protein = food.protein * scale,
                                                carbs = food.carbs * scale,
                                                fat = food.fat * scale
                                            ))
                                        } 
                                    },
                                    modifier = Modifier.weight(1f),
                                    label = { Text("Grams") },
                                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                                    )
                                )
                                
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${food.calories.toInt()} cal",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = CaloriesColor,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "P:${food.protein.toInt()} C:${food.carbs.toInt()} F:${food.fat.toInt()}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onAdd,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Check, null)
                Text("  Add to Diary", modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}

private suspend fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap? {
    return withContext(Dispatchers.IO) {
        try {
            if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            }
        } catch (e: Exception) {
            Log.e("PhotoScreen", "Error loading bitmap", e)
            null
        }
    }
}

// Extension to convert ImageProxy to Bitmap with rotation handled
private fun ImageProxy.toBitmap(): Bitmap {
    val buffer = planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)
    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    val matrix = Matrix()
    matrix.postRotate(imageInfo.rotationDegrees.toFloat())
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}
