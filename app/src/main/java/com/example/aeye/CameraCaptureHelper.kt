package com.example.aeye

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class CameraCaptureHelper(val imageCapture: ImageCapture, val context: Context, val viewModel: MainViewModel) {
    private val handler = Handler(Looper.getMainLooper())
    private val scope = CoroutineScope(Dispatchers.Main)
    private val imageCaptureRunnable = object : Runnable {
        override fun run() {
            scope.launch {
                captureFrame()
            }
            handler.postDelayed(this, 1000) // Capture frame every second
        }
    }

    fun startRecording() {
        handler.post(imageCaptureRunnable) // Start capturing frames
    }

    fun endRecording() {
        handler.removeCallbacks(imageCaptureRunnable) // Stop capturing frames
    }

    suspend fun captureFrame() = withContext(Dispatchers.IO) {
        val outputStream = ByteArrayOutputStream()
        val outputOptions = ImageCapture.OutputFileOptions.Builder(outputStream).build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exception: ImageCaptureException) {
                    // Handle error
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    viewModel.addImage(outputStream.toByteArray())
                    outputStream.close()
                }
            }
        )
    }
}