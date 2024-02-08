package com.example.aeye

import android.Manifest
import android.content.Context
import android.media.MediaRecorder
import android.os.Environment
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aeye.ui.theme.AEyeTheme
import java.io.File
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

private var recorder : AudioRecorder = AudioRecorder()

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun VoiceRecorderButton(onRecordingStarted: () -> Unit,
                        onRecordingCompleted: (ByteArray) -> Unit
) {
    val context = LocalContext.current
    var checked by remember { mutableStateOf(false) }
    val scale: Float by animateFloatAsState(if (checked) 1.5f else 1f)
    val permissionState = rememberPermissionState(android.Manifest.permission.RECORD_AUDIO)

    FilledIconToggleButton(
        checked = checked,
        onCheckedChange = {
            if (permissionState.status.isGranted) {
                checked = it
                if(it) {
                    onRecordingStarted();
                    recorder.startRecording(context);
                }
                else {
                    onRecordingCompleted(recorder.stopRecording())
                }
            }
        }
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
            modifier = Modifier.scale(scale)
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ExtendedFloatingVoiceRecorderButton(onRecordingStarted: () -> Unit,
                                onRecordingCompleted: (ByteArray) -> Unit
) {
    val context = LocalContext.current
    var checked by remember { mutableStateOf(false) }
    val scale: Float by animateFloatAsState(if (checked) 1.5f else 1f)
    val permissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)

    ExtendedFloatingActionButton(
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
        icon = { Icon(
            if (checked) Icons.Filled.Stop else Icons.Filled.Mic,
            if (checked) "Stop recording" else "Start recording",
            modifier = Modifier.size(36.dp)
        ) },
        text = { Text(text = if (checked) "Stop recording" else "Voice input", fontSize = 20.sp) },
    )
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
}