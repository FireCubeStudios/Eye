package com.example.aeye

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.aallam.openai.api.audio.SpeechRequest
import com.aallam.openai.api.audio.TranscriptionRequest
import com.aallam.openai.api.audio.Voice
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionChunk
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.chat.chatCompletionRequest
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import io.ktor.util.encodeBase64
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import okio.Buffer
import java.util.prefs.Preferences
import kotlin.time.Duration.Companion.seconds

const val KEY : String = "nuh uh"
const val InitialMessageGPT3I : String = "You are an AI assistant in a chat application where users can send text prompts along with multiple images. While you don’t have the capability to analyze images, you are aware that images are being processed elsewhere. Your task is to acknowledge the user’s request and let them know that their images are being processed. For example, if a user asks ‘what is in front of me right now?’ with images attached, you might respond, ‘I can help describe what’s in front of you. I’m currently processing the images. Please wait a moment.’ Your responses should be concise and informative."
const val InitialMessageGPT4 : String = "You are an AI assistant in an application that accepts speech-to-text input. The user can speak to the application, and you will receive this speech input to assist the user. Some users may be visually impaired, so ensure your assistance is accessible and inclusive. For example, if a user asks for a summary of a news article they heard about, provide a concise summary based on the information they provide in their speech."
const val InitialMessageGPT4V : String = "You are an assistant for an app that has speech to text input and camera input. For GPT-4: The user can point their camera and capture a frame per second while they speak, you will be given these frames and speech input to help the user. Try not to refer to these frames as image1, image 2 but instead pretend they are continuous as if a video was taken. Unless the user explicitly refers to a specific image. The user can be visually impaired so assist them in an accessible and inclusive manner."
class MainViewModel(application: Application) : AndroidViewModel(application){
    var chatMessages by mutableStateOf(listOf<IMessage>(
        Message(InitialMessageGPT4V, MessageType.System)
    ))
        private set

    var images by mutableStateOf(listOf<ByteArray>())
        private set

    val text = mutableStateOf("")

    var isPlaying = mutableStateOf(false)

    // Accessibility setting
    private val sharedPreferences = application.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
    var isAccessible by mutableStateOf(sharedPreferences.getBoolean("isAccessible", false))
        private set
    fun setIsAccessible(value: Boolean) {
        viewModelScope.launch {
            sharedPreferences.edit().putBoolean("isAccessible", value).apply()
            isAccessible = value
        }
    }

    var index = mutableStateOf(0)
    var navController : NavHostController? = null;


    val openAI = OpenAI(
        token = KEY,
        timeout = Timeout(socket = 60.seconds),
    )

    fun addImage(image : ByteArray) {
        images = images + image
    }

    fun clearImages() {
        images = listOf()
    }

    fun addMessage(text: String) {
        var completions: Flow<ChatCompletionChunk>? = null;

        if(images.isEmpty()) {
            chatMessages = chatMessages + Message(text, MessageType.User) // Add user message

            var chatCompletionRequest = ChatCompletionRequest(
                model = ModelId("gpt-3.5-turbo"),
                messages = chatMessages.map { message ->
                    ChatMessage(
                        content = message.text.value,
                        role = when (message.type) {
                            MessageType.User -> ChatRole.User
                            MessageType.Model -> ChatRole.Assistant
                            MessageType.System -> ChatRole.System
                            else -> ChatRole.System
                        },
                    )
                }
            )
            completions = openAI.chatCompletions(chatCompletionRequest)
        }
        else {
            chatMessages = chatMessages + ImageMessage(text, MessageType.User, images) // Add user message with images


            if(images.size > 2){ // show transient message because gpt 4 is long loading
                var chatCompletionRequest = ChatCompletionRequest(
                    model = ModelId("gpt-3.5-turbo"),
                    messages = listOf(
                        ChatMessage(
                            role = ChatRole.System,
                            content = InitialMessageGPT3I
                        ),
                        ChatMessage(
                            role = ChatRole.User,
                            content = text
                        )
                    )
                )
                var ccompletions = openAI.chatCompletions(chatCompletionRequest)

                val response = Message("",MessageType.Model)
                chatMessages = chatMessages + response // Add response message

                viewModelScope.launch {
                    ccompletions.collect { completion ->
                        completion.choices.forEach { choice ->
                            withContext(Dispatchers.Main) {
                                if (choice.delta.content != null)
                                    response.text.value += choice.delta.content
                            }
                        }
                    }
                    if(isAccessible){
                        player.stopReading();
                        val rawAudio: ByteArray = openAI.speech(
                            request = SpeechRequest(
                                model = ModelId("tts-1"),
                                input = response.text.value,
                                voice = Voice.Alloy,
                            )
                        )
                        player.startReading(rawAudio);
                    }
                }
            }
            clearImages()

            val chatCompletionRequest = chatCompletionRequest {
                model = ModelId("gpt-4-vision-preview")
                messages {
                    for (message in chatMessages) {
                        if (message.type == MessageType.User) {
                            user {
                                content {
                                    text(message.text.value)
                                    if (message is ImageMessage) {
                                        for (image in message.images) {
                                            val encoding = image.encodeBase64()
                                            image("data:image/jpeg;base64,$encoding")
                                        }
                                    }
                                }
                            }
                        } else if (message.type == MessageType.Model) {
                            assistant {
                                content = message.text.value
                            }
                        } else {
                            system {
                                content = message.text.value
                            }
                        }
                    }
                }
                maxTokens = 600
            }
            completions = openAI.chatCompletions(chatCompletionRequest)
        }
        try {
            if(isAccessible) {
                navController?.navigate(MainActivity.Screen.Screen1.route)
                index.value = 0
            }
        } catch (e: Exception) {
            // Handle or log the exception
        }

        val response = Message("", MessageType.Model)
        chatMessages = chatMessages + response // Add response message

        viewModelScope.launch {
            completions.collect { completion ->
                completion.choices.forEach { choice ->
                    withContext(Dispatchers.Main) {
                        if (choice.delta.content != null)
                            response.text.value += choice.delta.content
                    }
                }
            }
            if(isAccessible){
                player.stopReading();
                val rawAudio: ByteArray = openAI.speech(
                    request = SpeechRequest(
                        model = ModelId("tts-1"),
                        input = response.text.value,
                        voice = Voice.Alloy,
                    )
                )
                isPlaying.value = true
                player.onCompletionListener = {
                    isPlaying.value = false
                }
                player.startReading(rawAudio);
            }
        }
    }
    fun stopPlaying() {
        player.stopReading();
        isPlaying.value = false
    }

    fun clearMessages() {
        chatMessages = listOf()
        chatMessages = chatMessages + Message(InitialMessageGPT4V, MessageType.System) // Add the system message to the top
        showStatus("Messages cleared.", true)
    }

    suspend fun transcribe(audio : ByteArray) {
        val request = TranscriptionRequest(
            audio = FileSource(name = "audio.mp3", source = Buffer().write(audio)),
            model = ModelId("whisper-1"),
        )
        val transcription = openAI.transcription(request)
        text.value += " " + transcription.text;

        if(isAccessible){
            if (text.value.isNotEmpty()) {
                showStatus("Message sent.", true)
                addMessage(text.value)
                text.value = ""
            }
        }
    }

    val snackbarState = SnackbarHostState()
    private var player : AudioPlayer = AudioPlayer()
    fun showStatus(message: String, onlyAccessible: Boolean = false) {
        if(onlyAccessible)
            if(!isAccessible) // if onlyAccessible is true then only show the message with accessibility mode on
                return;
        viewModelScope.launch {
            snackbarState.showSnackbar(message = message)

           /* if(isAccessible){
                player.stopReading();
                val rawAudio: ByteArray = openAI.speech(
                    request = SpeechRequest(
                        model = ModelId("tts-1"),
                        input = message,
                        voice = Voice.Alloy,
                    )
                )
                player.startReading(rawAudio);
            }*/
        }
    }
}