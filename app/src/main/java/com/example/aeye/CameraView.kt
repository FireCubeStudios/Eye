package com.example.aeye

import android.Manifest
import android.content.Context
import android.view.TextureView
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
////////////////////////////////////import androidx.camera.core.Preview
import androidx.camera.core.UseCase
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
//import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aeye.ui.theme.AEyeTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraView(ViewModel : MainViewModel) {
    val permissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val imageCapture = remember { ImageCapture.Builder().build() }
    val cameraHelper = remember { CameraCaptureHelper(imageCapture, context, ViewModel) }
    val isLoading = remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        CameraPreview(Modifier.fillMaxSize(), imageCapture)
        if (!permissionState.status.isGranted) {
            PermissionsDialog(
                onDismissRequest = { },
                onConfirmation = { },
                permissionState,
                dialogTitle = "Camera permission required for this feature",
                dialogText = "Please grant the permission",
                icon = Icons.Filled.CameraAlt
            )
        }

        if (isLoading.value) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(1f)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
        ) {
            LargeFloatingActionButton(
                onClick = {
                    isLoading.value = true
                    coroutineScope.launch {
                        cameraHelper.captureFrame()
                    }
                    isLoading.value = false
                    ViewModel.showStatus("Capture taken.")
                },
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.secondary,
            ) {
                Icon(Icons.Filled.Camera, contentDescription = "Camera", modifier = Modifier.size(36.dp))
            }
            FloatingVoiceRecorderButton(onRecordingStarted = { ->
                cameraHelper.startRecording()
                ViewModel.showStatus("Camera recording started", true) },
                onRecordingCompleted = { audio : ByteArray ->
                    isLoading.value = true
                    cameraHelper.endRecording()
                    coroutineScope.launch {
                        ViewModel.transcribe(audio)
                    }
                    isLoading.value = false
                    ViewModel.showStatus("Camera recording complete.")
                })
            LargeFloatingActionButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.secondary,
                onClick = { },
            ) {
                Icon(Icons.Filled.HelpOutline, contentDescription = "Help", modifier = Modifier.size(36.dp))
            }
        }
    }
}