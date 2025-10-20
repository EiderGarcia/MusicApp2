package com.example.musicapp

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.media.AudioManager
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState


// -------------------------
// Portrait Layout
// -------------------------
@Composable
fun MusicPlayerPortraitScreen(
    mediaPlayer: MediaPlayer,
    isPlaying: Boolean,
    onPlayPause: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val audioManager = context.getSystemService(android.content.Context.AUDIO_SERVICE) as AudioManager

    var sliderPosition by remember { mutableStateOf(0.25f) }
    var currentTime by remember { mutableStateOf("0:00") }
    val totalTime = formatTime(mediaPlayer.duration)

    LaunchedEffect(Unit) {
        val quarter = (mediaPlayer.duration * 0.25).toInt()
        mediaPlayer.seekTo(quarter)
        currentTime = formatTime(quarter)
    }

    DisposableEffect(isPlaying) {
        val handler = Handler(Looper.getMainLooper())
        val updateTask = object : Runnable {
            override fun run() {
                if (mediaPlayer.isPlaying) {
                    val pos = mediaPlayer.currentPosition.toFloat() / mediaPlayer.duration
                    sliderPosition = pos
                    currentTime = formatTime(mediaPlayer.currentPosition)
                }
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(updateTask)
        onDispose { handler.removeCallbacksAndMessages(null) }
    }

    var showVolumeBar by remember { mutableStateOf(false) }
    var volumeLevel by remember { mutableStateOf(0.7f) }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Image(
            painter = painterResource(R.drawable.background),
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "The Rise and Fall of a Midwest Princess",
                color = Color(0xFFA9A8A8),
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))

            val albumSize = 260.dp
            val barWidth = 8.dp
            val barHeight = 200.dp
            val barSpacing = 12.dp

            Box(
                modifier = Modifier.fillMaxWidth().height(albumSize),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.the_rise_and_fall_of_a_midwest_princess),
                    contentDescription = "Album Cover",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(albumSize).clip(RoundedCornerShape(16.dp))
                )
                Box(
                    modifier = Modifier.offset(x = -(albumSize / 2 + barSpacing)).height(albumSize).width(40.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Column(
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        if (showVolumeBar) {
                            Box(
                                modifier = Modifier.height(barHeight).width(barWidth).background(Color.Gray, RoundedCornerShape(4.dp))
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxWidth().fillMaxHeight(volumeLevel)
                                        .background(Color.White, RoundedCornerShape(4.dp))
                                        .align(Alignment.BottomCenter)
                                )
                                Box(
                                    modifier = Modifier.size(16.dp).background(Color.White, CircleShape)
                                        .align(Alignment.BottomCenter)
                                        .offset(y = (-volumeLevel * barHeight.value).dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        IconButton(onClick = { showVolumeBar = !showVolumeBar }, modifier = Modifier.size(48.dp)) {
                            Icon(
                                painter = painterResource(android.R.drawable.ic_lock_silent_mode_off),
                                contentDescription = "Silent Mode",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("After Midnight", color = Color(0xFFFAEFE0), fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("Chappell Roan", color = Color(0xFFFAEFE0), fontSize = 16.sp)

            Spacer(modifier = Modifier.height(24.dp))

            // Top buttons: Lyrics + Queue / Share
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {}) {
                        Icon(painterResource(R.drawable.song_lyrics), "Lyrics", tint = Color.White, modifier = Modifier.size(28.dp))
                    }
                    IconButton(onClick = {}) {
                        Icon(painterResource(R.drawable.playing_next), "Queue", tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                }
                IconButton(onClick = {}) {
                    Icon(painterResource(android.R.drawable.ic_menu_share), "Share", tint = Color.White, modifier = Modifier.size(36.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // SeekBar + Time
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Slider(
                    value = sliderPosition,
                    onValueChange = {
                        sliderPosition = it
                        val newPos = (mediaPlayer.duration * it).toInt()
                        mediaPlayer.seekTo(newPos)
                        currentTime = formatTime(newPos)
                    },
                    valueRange = 0f..1f,
                    modifier = Modifier.fillMaxWidth(0.9f)
                )

                Row(Modifier.fillMaxWidth(0.9f), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(currentTime, color = Color(0xFFFAEFE0), fontSize = 14.sp)
                    Text(totalTime, color = Color(0xFFFAEFE0), fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Playback controls
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                IconButton(onClick = {
                    mediaPlayer.seekTo((mediaPlayer.currentPosition - 5000).coerceAtLeast(0))
                }, modifier = Modifier.size(80.dp)) {
                    Icon(painterResource(R.drawable.previous), "Previous", tint = Color.White, modifier = Modifier.size(80.dp))
                }

                IconButton(onClick = {
                    if (mediaPlayer.isPlaying) {
                        mediaPlayer.pause()
                        onPlayPause(false)
                    } else {
                        mediaPlayer.start()
                        onPlayPause(true)
                    }
                }, modifier = Modifier.size(104.dp)) {
                    Icon(
                        painterResource(if (isPlaying) R.drawable.pause else R.drawable.play),
                        "Play/Pause",
                        tint = Color.White,
                        modifier = Modifier.size(104.dp)
                    )
                }

                IconButton(onClick = {
                    mediaPlayer.seekTo((mediaPlayer.currentPosition + 5000).coerceAtMost(mediaPlayer.duration))
                }, modifier = Modifier.size(100.dp)) {
                    Icon(painterResource(R.drawable.next), "Next", tint = Color.White, modifier = Modifier.size(80.dp))
                }
            }
        }
    }
}

// -------------------------
// Landscape Layout (Horizontal)
// -------------------------

@Composable
fun MusicPlayerLandscapeScreen(
    mediaPlayer: MediaPlayer,
    isPlaying: Boolean,
    onPlayPause: (Boolean) -> Unit
) {
    val context = LocalContext.current
    var sliderPosition by remember { mutableStateOf(0.25f) }
    var currentTime by remember { mutableStateOf("0:00") }
    val totalTime = formatTime(mediaPlayer.duration)

    LaunchedEffect(Unit) {
        val quarter = (mediaPlayer.duration * 0.25).toInt()
        mediaPlayer.seekTo(quarter)
        currentTime = formatTime(quarter)
    }

    DisposableEffect(isPlaying) {
        val handler = Handler(Looper.getMainLooper())
        val updateTask = object : Runnable {
            override fun run() {
                if (mediaPlayer.isPlaying) {
                    val pos = mediaPlayer.currentPosition.toFloat() / mediaPlayer.duration
                    sliderPosition = pos
                    currentTime = formatTime(mediaPlayer.currentPosition)
                }
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(updateTask)
        onDispose { handler.removeCallbacksAndMessages(null) }
    }

    var showVolumeBar by remember { mutableStateOf(false) }
    var volumeLevel by remember { mutableStateOf(0.7f) }

    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // Background
        Image(
            painter = painterResource(R.drawable.background),
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(350.dp)
                        .height(300.dp)
                        .background(Color(0xBB421D0C), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column(modifier = Modifier.verticalScroll(scrollState)) {
                        Text(
                            text = "My mama said, &quot;Nothing good happens\\n\n" +
                                    "When it's late and you're dancing alone&quot;\\n\n" +
                                    "She's in my head saying, &quot;It's not attractive\\n\n" +
                                    "Wearing that dress and red lipstick&quot;\\n\\n\n" +
                                    "\n" +
                                    "This is what I wanted, this is what I like\\n\n" +
                                    "I've been a good, good girl for a long time\\n\n" +
                                    "But baby, I like flirting, a lover by my side\\n\n" +
                                    "Can't be a good, good girl even if I tried\\n\\n\n" +
                                    "\n" +
                                    "'Cause after midnight\\n\n" +
                                    "I'm feeling kinda freaky, maybe it's the club lights\\n\n" +
                                    "I kinda wanna kiss your girlfriend if you don't mind\\n\n" +
                                    "I love a little drama, let's start a bar fight\\n\n" +
                                    "'Cause everything good happens after midnight\\n\\n\n" +
                                    "I'm feeling kinda freaky, maybe it's the moonlight\\n\n" +
                                    "I kinda wanna kiss your boyfriend if you don't mind\\n\n" +
                                    "I love a little &quot;uh-huh&quot;, let's watch the sunrise\\n\n" +
                                    "'Cause everything good happens after\\n\\n\n" +
                                    "\n" +
                                    "I really want your hands on my body\\n\n" +
                                    "A slow dance, baby, let's get it on\\n\n" +
                                    "That’s my type of fun, that's my kind of party\\n\n" +
                                    "Your hands on my body, your hot hands",
                            color = Color(0xFFFAEFE0),
                            fontSize = 16.sp,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Album name
                    Text(
                        text = "The Rise and Fall of a Midwest Princess",
                        color = Color(0xFFA9A8A8),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Box {
                        Image(
                            painter = painterResource(R.drawable.the_rise_and_fall_of_a_midwest_princess),
                            contentDescription = "Album Cover",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(200.dp)
                                .clip(RoundedCornerShape(16.dp))
                        )

                        // Volume bar + button overlay on bottom-left of cover
                        Box(
                            modifier = Modifier.align(Alignment.BottomStart),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                if (showVolumeBar) {
                                    Box(
                                        modifier = Modifier
                                            .height(200.dp)
                                            .width(8.dp)
                                            .background(Color.Gray, RoundedCornerShape(4.dp))
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .fillMaxHeight(volumeLevel)
                                                .background(Color.White, RoundedCornerShape(4.dp))
                                                .align(Alignment.BottomCenter)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(16.dp)
                                                .background(Color.White, CircleShape)
                                                .align(Alignment.BottomCenter)
                                                .offset(y = (-volumeLevel * 200.dp.value).dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                }

                                IconButton(
                                    onClick = { showVolumeBar = !showVolumeBar },
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(android.R.drawable.ic_lock_silent_mode_off),
                                        contentDescription = "Volume",
                                        tint = Color.White,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Song title + artist
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "After Midnight",
                            color = Color(0xFFFAEFE0),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Chappell Roan",
                            color = Color(0xFFFAEFE0),
                            fontSize = 16.sp
                        )
                    }
                }

                // Bottom content: seekbar + buttons
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Seekbar with elapsed / total time
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        Text(currentTime, color = Color(0xFFFAEFE0), fontSize = 14.sp)
                        Slider(
                            value = sliderPosition,
                            onValueChange = {
                                sliderPosition = it
                                val newPos = (mediaPlayer.duration * it).toInt()
                                mediaPlayer.seekTo(newPos)
                                currentTime = formatTime(newPos)
                            },
                            valueRange = 0f..1f,
                            modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                        )
                        Text(totalTime, color = Color(0xFFFAEFE0), fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Playback buttons: bigger sizes
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        IconButton(onClick = { mediaPlayer.seekTo((mediaPlayer.currentPosition - 5000).coerceAtLeast(0)) }) {
                            Icon(painterResource(R.drawable.previous), "Previous", tint = Color.White, modifier = Modifier.size(80.dp))
                        }
                        IconButton(onClick = {
                            if (mediaPlayer.isPlaying) {
                                mediaPlayer.pause()
                                onPlayPause(false)
                            } else {
                                mediaPlayer.start()
                                onPlayPause(true)
                            }
                        }) {
                            Icon(painterResource(if (isPlaying) R.drawable.pause else R.drawable.play), "Play/Pause", tint = Color.White, modifier = Modifier.size(104.dp))
                        }
                        IconButton(onClick = { mediaPlayer.seekTo((mediaPlayer.currentPosition + 5000).coerceAtMost(mediaPlayer.duration)) }) {
                            Icon(painterResource(R.drawable.next), "Next", tint = Color.White, modifier = Modifier.size(80.dp))
                        }
                    }
                }
            }
        }
    }
}


fun formatTime(ms: Int): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%d:%02d", minutes, seconds)
}
