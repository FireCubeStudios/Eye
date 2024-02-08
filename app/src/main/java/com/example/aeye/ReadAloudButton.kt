package com.example.aeye

import android.content.Context
import android.media.MediaRecorder
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.aallam.openai.api.audio.SpeechRequest
import com.aallam.openai.api.audio.Voice
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.example.aeye.ui.theme.AEyeTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import kotlin.time.Duration.Companion.seconds

private var player : AudioPlayer = AudioPlayer()

@Composable
fun ReadAloudButton(
    modifier: Modifier = Modifier,
    text: String
) {
    val coroutineScope = rememberCoroutineScope()
    var checked by remember { mutableStateOf(false) }
    val isLoading = remember { mutableStateOf(false) }
    var job: Job? = null

    // Update 'checked' when the audio finishes playing
    DisposableEffect(player) {
        player.onCompletionListener = { checked = false }
        onDispose { player.onCompletionListener = null }
    }

    OutlinedButton(
        onClick = {
            checked = !checked
            if(checked)
                job = coroutineScope.launch {
                    isLoading.value = true;
                    val rawAudio: ByteArray = openAI.speech(
                        request = SpeechRequest(
                            model = ModelId("tts-1"),
                            input = text,
                            voice = Voice.Alloy,
                        )
                    )
                    isLoading.value = false;
                    player.onCompletionListener = {
                        checked = false;
                    }
                    player.startReading(rawAudio);
                }
            else {
                isLoading.value = false;
                job?.cancel()
                player.stopReading()
            }
        },
        modifier = modifier
    ) {
        if (isLoading.value) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(24.dp))
        }
        else {
            Row(verticalAlignment = Alignment.CenterVertically,)
            {
                Icon(
                    imageVector = if (checked) Icons.Filled.Stop else Icons.Filled.VolumeUp,
                    contentDescription = "Read aloud"
                )
                Text(
                    if (checked) "Stop" else "Read aloud",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Preview(showBackground = false)
@Composable
fun ReadAloudButtonPreview() {
    AEyeTheme {
        ReadAloudButton(modifier = Modifier.padding(start= 8.dp), "test text")
    }
}

/*
    Start of reading out loud logic
*/

val openAI = OpenAI(
    token = KEY,
    timeout = Timeout(socket = 60.seconds),
)