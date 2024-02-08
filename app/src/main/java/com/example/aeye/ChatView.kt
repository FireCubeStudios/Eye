package com.example.aeye

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aeye.ui.theme.AEyeTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatView(ViewModel : MainViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val isLoading = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    // Scroll to bottom of list when new items added
    val listState = rememberLazyListState()
    LaunchedEffect(ViewModel.chatMessages) {
        if(ViewModel.chatMessages.size > 0)
            listState.animateScrollToItem(ViewModel.chatMessages.size - 1)
    }

    Box(
        Modifier.fillMaxSize()
    ){
        Scaffold(
            topBar = {
                if(ViewModel.isAccessible) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .padding(16.dp, 16.dp, 16.dp, 8.dp)
                    ) {
                        Text(
                            text = "Accessibility mode",
                            fontSize = 24.sp,
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.align(Alignment.CenterEnd)
                        )
                        {
                            if(ViewModel.isPlaying.value) {
                                FloatingActionButton(
                                    onClick = { ViewModel.stopPlaying() },
                                ) {
                                    Icon(Icons.Filled.Stop, "Stop button")
                                }
                            }
                            FloatingActionButton(
                                onClick = { showBottomSheet = true },
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Icon(Icons.Filled.Settings, "Settings button")
                            }
                        }
                    }
                }
                else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .padding(16.dp, 16.dp, 16.dp, 8.dp)
                    ) {
                        Text(
                            text = "Welcome",
                            fontSize = 24.sp,
                            modifier = Modifier.align(Alignment.CenterStart)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.align(Alignment.CenterEnd)
                        )
                        {
                            ElevatedButton(
                                onClick = { ViewModel.clearMessages() },
                                ) {
                                Row(verticalAlignment = Alignment.CenterVertically)
                                {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = "New chat"
                                    )
                                    Text(
                                        "New chat",
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                            }
                            IconButton(
                                onClick = { showBottomSheet = true },
                            ) {
                                Icon(
                                    Icons.Default.Settings,
                                    contentDescription = "Settings"
                                )
                            }
                        }
                    }
                }
            },
            bottomBar = {
                Column(
                    modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .padding(4.dp, 8.dp, 4.dp, 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if(ViewModel.images.isNotEmpty())
                            IconButton(onClick = { ViewModel.clearImages() }) {
                                Icon(Icons.Filled.Clear, "Clear all images")
                            }
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(end = 16.dp),
                            modifier = Modifier.padding(16.dp, 8.dp, 16.dp, 0.dp),
                        ) {
                            items(ViewModel.images) { image ->
                                ImageCard(image)
                            }
                        }
                    }
                    if(ViewModel.isAccessible){
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                                .padding(8.dp)
                        ) {
                            ExtendedFloatingActionButton(
                                onClick = { ViewModel.clearMessages() },
                                icon = { Icon(Icons.Filled.Add, "New chat button") },
                                text = { Text(text = "New chat") },
                                modifier = Modifier.align(Alignment.CenterStart)
                            )
                            Box(
                                modifier = Modifier.align(Alignment.Center).padding(12.dp)
                            ) {
                                ExtendedFloatingVoiceRecorderButton(onRecordingStarted = { ->
                                    focusManager.clearFocus()
                                    ViewModel.showStatus("Voice recording started", true)
                                },
                                    onRecordingCompleted = { audio: ByteArray ->
                                        coroutineScope.launch {
                                            isLoading.value = true
                                            ViewModel.transcribe(audio)
                                            isLoading.value = false
                                            ViewModel.showStatus("Voice recording finished", true)
                                        }
                                    })
                            }
                            ExtendedFloatingActionButton(
                                onClick = {   // Cursed code duplication fix later
                                    if (ViewModel.text.value.isNotEmpty()) {
                                        ViewModel.showStatus("Message sent.", true)
                                        ViewModel.addMessage(ViewModel.text.value)
                                        ViewModel.text.value = ""
                                        focusManager.clearFocus()
                                    }
                                },
                                icon = { Icon(Icons.Filled.Send, "Submit text button") },
                                text = { Text(text = "Submit text") },
                                modifier = Modifier.align(Alignment.CenterEnd)
                            )
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        if(!ViewModel.isAccessible) {
                            VoiceRecorderButton(onRecordingStarted = { ->
                                focusManager.clearFocus()
                                ViewModel.showStatus("Voice recording started", true)
                            },
                                onRecordingCompleted = { audio: ByteArray ->
                                    coroutineScope.launch {
                                        isLoading.value = true
                                        ViewModel.transcribe(audio)
                                        isLoading.value = false
                                        ViewModel.showStatus("Voice recording finished", true)
                                    }
                                })
                        }
                        OutlinedTextField(
                            value = ViewModel.text.value,
                            onValueChange = { ViewModel.text.value = it },
                            Modifier
                                .weight(1f)
                                .padding(8.dp, 0.dp, 8.dp, 0.dp),
                            placeholder = { Text(text = "Type your text here...") },
                            maxLines = Int.MAX_VALUE
                        )
                        if(!ViewModel.isAccessible) {
                            FilledIconButton(
                                onClick = {
                                    if (ViewModel.text.value.isNotEmpty()) {
                                        ViewModel.showStatus("Message sent.", true)
                                        ViewModel.addMessage(ViewModel.text.value)
                                        ViewModel.text.value = ""
                                        focusManager.clearFocus()
                                    }
                                }
                            ) {
                                Icon(Icons.Filled.Send, "Send")
                            }
                        }
                    }
                }
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .align(Alignment.Center)
                .background(MaterialTheme.colorScheme.secondaryContainer),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    state = listState,
                    contentPadding = PaddingValues(bottom = 16.dp),
                    modifier = Modifier.padding(16.dp, 8.dp, 16.dp, 0.dp),
                ) {
                    items(ViewModel.chatMessages) { message ->
                        MessageCard(message)
                    }
                }
            }
            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showBottomSheet = false },
                    sheetState = sheetState
                ) {
                    Column(
                        Modifier.fillMaxWidth()
                            .padding(16.dp, 16.dp, 16.dp, 60.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(Modifier.fillMaxWidth().padding(16.dp)){
                            Text(
                                "Enable visual accessibility mode",
                                fontSize = 20.sp,
                                modifier = Modifier.padding(8.dp, 8.dp, 0.dp, 0.dp)
                            )
                            Switch(
                                checked = ViewModel.isAccessible,
                                onCheckedChange = { ViewModel.setIsAccessible(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                                    uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
                                    uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                                ),
                                modifier = Modifier.align(Alignment.CenterEnd)
                            )
                        }
                        Text(
                            text = """
                                This mode will enable:
                                - Chat responses are automatically read aloud
                                - Voice or visual input is automatically sent skipping the need to manually send
                                - Auditory notifications on interactions such as "Microphone recording started"
                                - Note: Audio for notifications turned off in this build
                                - More accessible UI with larger buttons and labels
                        
                                This mode is designed for the visually impaired and those who prefer using this app to communicate directly instead of text input.
                            """.trimIndent(),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
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
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatViewPreview() {
    val viewModel: MainViewModel = viewModel()
    AEyeTheme {
        ChatView(viewModel)
    }
}