// MusicViewModel.kt
package com.example.musicapp

import android.app.Application
import android.media.AudioManager
import android.media.MediaPlayer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class MusicViewModel(application: Application) : AndroidViewModel(application) {

    val mediaPlayer: MediaPlayer = MediaPlayer.create(application, R.raw.after_midnight)
    val isPlaying = MutableLiveData(false)
    val currentPosition = MutableLiveData(0)
    val totalDuration = MutableLiveData(mediaPlayer.duration)

    private val audioManager = application.getSystemService(AudioManager::class.java)
    val maxVolume = audioManager?.getStreamMaxVolume(AudioManager.STREAM_MUSIC) ?: 15

    init {
        val startPos = mediaPlayer.duration / 4
        mediaPlayer.seekTo(startPos)
        currentPosition.value = startPos

        mediaPlayer.setOnCompletionListener {
            isPlaying.value = false
        }
    }

    fun play() {
        mediaPlayer.start()
        isPlaying.value = true
    }

    fun pause() {
        mediaPlayer.pause()
        isPlaying.value = false
    }

    fun seekTo(pos: Int) {
        mediaPlayer.seekTo(pos)
        currentPosition.value = pos
    }

    fun setVolume(percent: Int) {
        audioManager?.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            (percent / 100.0 * maxVolume).toInt(),
            0
        )
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
    }
}
