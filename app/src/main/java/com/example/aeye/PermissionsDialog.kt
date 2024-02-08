package com.example.aeye

import android.Manifest
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.example.aeye.ui.theme.AEyeTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun PermissionsDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    permissionState : PermissionState,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
) {
    AlertDialog(
        icon = {
            Icon(icon, contentDescription = "Permission Icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    permissionState.launchPermissionRequest()
                    onConfirmation()
                }
            ) {
                Text("Accept")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Preview(showBackground = false)
@Composable
fun PermissionsDialogPreview() {
    val permissionState = rememberPermissionState(
        Manifest.permission.RECORD_AUDIO
    )
    AEyeTheme {
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