package com.example.aeye

import android.content.Context
import android.media.MediaRecorder
import java.io.File

class AudioRecorder {
    private var recorder: MediaRecorder? = null
    private var tempFile: File? = null

    fun startRecording(context: Context) {
        tempFile = File.createTempFile("myrecording", ".mp3", context.cacheDir)
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(tempFile?.absolutePath)
            prepare()
            start()
        }
    }

    fun stopRecording(): ByteArray {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
        val bytes = tempFile?.readBytes() ?: ByteArray(0)
        tempFile?.delete()
        return bytes
    }
}
