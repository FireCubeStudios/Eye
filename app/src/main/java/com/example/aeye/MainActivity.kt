package com.example.aeye

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.speech.tts.Voice
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.mutableStateOf
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.aeye.ui.theme.AEyeTheme
import com.microsoft.device.dualscreen.twopanelayout.TwoPaneLayout
import com.microsoft.device.dualscreen.twopanelayout.TwoPaneLayoutNav
import com.microsoft.device.dualscreen.twopanelayout.TwoPaneMode
import com.microsoft.device.dualscreen.windowstate.rememberWindowState
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AEyeTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val viewModel: MainViewModel = viewModel()
                    App(viewModel)
                }
            }
        }
    }

    // Boilerplate for navigation
    sealed class Screen(val route: String) {
        object Screen1 : Screen("screen1")
        object Screen2 : Screen("screen2")
    }

    @Composable
    fun App(ViewModel : MainViewModel) {
        val windowState = rememberWindowState()
        if (windowState.isDualScreen()){
            TwoPaneLayout(
                paneMode = TwoPaneMode.HorizontalSingle,
                pane1 = { ChatView(ViewModel) },
                pane2 = {
                    Scaffold(
                        snackbarHost = {
                            SnackbarHost(hostState = ViewModel.snackbarState) { data ->
                                Snackbar(
                                    snackbarData = data
                                )
                            }
                        },
                        content = { innerPadding ->
                            Box(
                                modifier = Modifier.fillMaxSize().padding(innerPadding)
                            ) {
                                SnackbarHost(hostState = ViewModel.snackbarState) { data ->
                                    Snackbar(
                                        snackbarData = data
                                    )
                                }
                                CameraView(ViewModel)
                            }
                        }
                    )
                }
            )
        } else {
            AppNavigation(ViewModel)
        }
    }

    @Composable
    fun AppNavigation(ViewModel : MainViewModel) {
        ViewModel.navController = rememberNavController()

        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = ViewModel.snackbarState) { data ->
                    Snackbar(
                        snackbarData = data
                    )
                }
            },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = ViewModel.index.value == 0,
                        icon = { Icon(Icons.Filled.ChatBubble, contentDescription = "Chat page") },
                        label = { Text("Chat") },
                        onClick = {
                            ViewModel.index.value = 0
                            ViewModel.navController?.navigate(Screen.Screen1.route) }
                    )
                    NavigationBarItem(
                        selected = ViewModel.index.value == 1,
                        icon = { Icon(Icons.Filled.CameraAlt, contentDescription = "Camera page") },
                        label = { Text("Camera") },
                        onClick = {
                            ViewModel.index.value = 1
                            ViewModel.navController?.navigate(Screen.Screen2.route)
                        }
                    )
                }
            }
        ) { innerPadding ->
            NavHost(ViewModel.navController!!, startDestination = Screen.Screen1.route, Modifier.padding(innerPadding)) {
                composable(Screen.Screen1.route) { ChatView(ViewModel) }
                composable(Screen.Screen2.route) { CameraView(ViewModel) }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun AppPreview() {
        val viewModel: MainViewModel = viewModel()
        AEyeTheme {
            App(viewModel)
        }
    }
}