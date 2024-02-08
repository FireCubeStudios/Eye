package com.example.aeye

class ImageMessage(
    text: String,
    type: MessageType,
    val images: List<ByteArray>
) : Message(text, type)