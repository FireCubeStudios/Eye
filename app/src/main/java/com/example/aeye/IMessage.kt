package com.example.aeye

import androidx.compose.runtime.MutableState

interface IMessage {
    var text: MutableState<String>
    val type: MessageType
}