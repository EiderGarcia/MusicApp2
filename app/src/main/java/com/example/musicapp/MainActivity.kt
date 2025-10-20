package com.example.musicapp

import android.content.res.Configuration
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current

            // Shared state
            val mediaPlayer = remember { MediaPlayer.create(context, R.raw.after_midnight) }
            var isPlaying by remember { mutableStateOf(false) }

            // Orientation-aware layout
            val orientation = context.resources.configuration.orientation
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                MusicPlayerLandscapeScreen(
                    mediaPlayer = mediaPlayer,
                    isPlaying = isPlaying,
                    onPlayPause = { isPlaying = it }
                )
            } else {
                MusicPlayerPortraitScreen(
                    mediaPlayer = mediaPlayer,
                    isPlaying = isPlaying,
                    onPlayPause = { isPlaying = it }
                )
            }
        }
    }
}
