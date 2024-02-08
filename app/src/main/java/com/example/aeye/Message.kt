package com.example.aeye

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf


open class Message(override var type: MessageType) : IMessage {
    override var text: MutableState<String> = mutableStateOf("")

    constructor(text: String, type: MessageType) : this(type) {
        this.text.value = text
    }
}