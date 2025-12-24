package com.example.mediapipe.examples.macrosagent.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import android.R

@Composable
fun CameraScreen(
    onImageCaptured: (Bitmap) -> Unit,
    onMockCaptured: () -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                }
            },
            modifier = Modifier.fillMaxSize(),
            update = { previewView ->
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageCapture
                    )
                } catch (exc: Exception) {
                    Log.e("CameraScreen", "Use case binding failed", exc)
                }
            }
        )

        FloatingActionButton(
            onClick = onMockCaptured,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(32.dp),
            containerColor = Color(0xFF4CAF50)
        ) {
            Text("Mock", color = Color.White, modifier = Modifier.padding(horizontal = 8.dp))
        }

        FloatingActionButton(
            onClick = onMockCaptured,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(32.dp),
            containerColor = Color(0xFF4CAF50)
        ) {
            Text("Mock", color = Color.White, modifier = Modifier.padding(horizontal = 8.dp))
        }

        FloatingActionButton(
            onClick = {
                takePhoto(
                    imageCapture = imageCapture,
                    executor = ContextCompat.getMainExecutor(context),
                    onImageCaptured = onImageCaptured,
                    onError = { Log.e("CameraScreen", "Error capturing image", it) }
                )
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(32.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
           Icon(
               painter = painterResource(android.R.drawable.ic_menu_camera),
               contentDescription = "Capture",
               tint = Color.White
           )
        }
    }
}

private fun takePhoto(
    imageCapture: ImageCapture,
    executor: Executor,
    onImageCaptured: (Bitmap) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {
    imageCapture.takePicture(
        executor,
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onError(exception: ImageCaptureException) {
                onError(exception)
            }

            override fun onCaptureSuccess(image: ImageProxy) {
                val bitmap = image.toBitmap()
                // Rotate if needed based on rotationDegrees, ImageProxy handles this reasonably well usually but matrix adjustment may be needed for exact correctness. 
                // For simplicity in this vibe code, passing the bitmap directly.
                // Note: ImageProxy.toBitmap() handles rotation in newer CameraX versions automatically or needs helper.
                
                // Manually rotating just in case as ImageProxy.toBitmap() might not apply rotation info from Exif for OnImageCapturedCallback in all versions
                val rotationDegrees = image.imageInfo.rotationDegrees
                val rotatedBitmap = if (rotationDegrees != 0) {
                     val matrix = Matrix()
                     matrix.postRotate(rotationDegrees.toFloat())
                     Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                } else {
                    bitmap
                }
                
                onImageCaptured(rotatedBitmap)
                image.close()
            }
        }
    )
}
