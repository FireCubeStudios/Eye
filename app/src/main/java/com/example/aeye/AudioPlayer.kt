package com.example.aeye

import android.media.MediaPlayer
import java.io.File
import java.io.FileOutputStream

class AudioPlayer {
    private var mediaPlayer: MediaPlayer? = null
    var onCompletionListener: (() -> Unit)? = null

    fun startReading(audioData: ByteArray) {
        // Create a temporary file and write the audio data to it
        val tempFile = File.createTempFile("audio", "mp3")
        FileOutputStream(tempFile).use { it.write(audioData) }

        // Create a new MediaPlayer
        mediaPlayer = MediaPlayer().apply {
            setDataSource(tempFile.absolutePath)
            prepare()
            start()
            setOnCompletionListener {
                onCompletionListener?.invoke()
            }
        }
    }

    fun stopReading() {
        // Stop playback and release the MediaPlayer
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}

