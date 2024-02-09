package com.example.aeye

import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.aeye.ui.theme.AEyeTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun AboutDialog(
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        icon = {
            Icon(Icons.Filled.Help, contentDescription = "Permission Icon")
        },
        title = {
            Text(text = "Input help")
        },
        text = {
            Column() {
                Text(
                    text = """
                                There are 2 buttons for input as shown below:
                                - The camera icon button allows you to take individual snapshots to send as input
                                - The mic icon takes a recording of your speech input alongside taking 1 snapshot per second of the camera
                            """.trimIndent(),
                    modifier = Modifier.padding(16.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    LargeFloatingActionButton(
                        onClick = { },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.secondary,
                    ) {
                        Icon(Icons.Filled.Camera, contentDescription = "Camera", modifier = Modifier.size(36.dp))
                    }
                    LargeFloatingActionButton(
                        onClick = { },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.secondary,
                    ) {
                        Icon(Icons.Filled.Mic, contentDescription = "Camera", modifier = Modifier.size(36.dp))
                    }
                }
            }

        },
        onDismissRequest = { onDismissRequest() },
        confirmButton = {
            TextButton( onClick = { onDismissRequest() } ) {
                Text("Ok")
            }
        }
    )
}