package com.example.aeye

import android.Manifest
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.aallam.openai.api.audio.SpeechRequest
import com.aallam.openai.api.audio.Voice
import com.aallam.openai.api.model.ModelId
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch

private var recorder : AudioRecorder = AudioRecorder()

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FloatingVoiceRecorderButton(onRecordingStarted: () -> Unit,
                        onRecordingCompleted: (ByteArray) -> Unit
) {
    val context = LocalContext.current
    var checked by remember { mutableStateOf(false) }
    val scale: Float by animateFloatAsState(if (checked) 1.5f else 1f)
    val permissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)

    FloatingActionButton(
        onClick = {
            checked = !checked
            if (permissionState.status.isGranted) {
                if(checked) {
                    onRecordingStarted();
                    recorder.startRecording(context);
                }
                else {
                    onRecordingCompleted(recorder.stopRecording())
                }
            }
        },
        Modifier.size(120.dp)
    ) {
        if (!permissionState.status.isGranted) {
            PermissionsDialog(
                onDismissRequest = { },
                onConfirmation = { },
                permissionState,
                dialogTitle = "Audio permission required for this feature",
                dialogText = "Please grant the permission",
                icon = Icons.Filled.Mic
            )
        }
        Icon(
            imageVector = if (checked) Icons.Filled.Stop else Icons.Filled.Mic,
            contentDescription = if (checked) "Stop recording" else "Start recording",
            modifier = Modifier.size(48.dp).scale(scale)
        )
    }
}