package com.example.musicapp

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MusicViewModel
    private lateinit var seekBar: SeekBar
    private lateinit var currentTimeText: TextView
    private lateinit var totalTimeText: TextView
    private lateinit var volumeBar: SeekBar
    private lateinit var volumeIcon: ImageView
    private lateinit var playButton: ImageView

    private val handler = Handler(Looper.getMainLooper())
    private val updateSeekBar = object : Runnable {
        override fun run() {
            if (viewModel.isPlaying.value == true) {
                viewModel.currentPosition.value = viewModel.mediaPlayer.currentPosition
            }
            handler.postDelayed(this, 1000)
        }
    }

    // Extension function to load images from assets
    private fun ImageView.loadFromAssets(fileName: String) {
        try {
            val inputStream = context.assets.open(fileName)
            val drawable = Drawable.createFromStream(inputStream, null)
            this.setImageDrawable(drawable)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize views
        playButton = findViewById(R.id.Play)
        val backgroundImage: ImageView = findViewById(R.id.Background)
        val coverImage: ImageView = findViewById(R.id.Cover)
        val previousButton: ImageView = findViewById(R.id.Previous)
        val nextButton: ImageView = findViewById(R.id.Next)
        val QueueButton: ImageView = findViewById(R.id.Queue)
        val LyricsButton: ImageView = findViewById(R.id.Lyrics)
        volumeBar = findViewById(R.id.VolumeBar)
        volumeIcon = findViewById(R.id.VolumeIcon)
        seekBar = findViewById(R.id.seekBar)
        currentTimeText = findViewById(R.id.Progress)
        totalTimeText = findViewById(R.id.Duration)

        // Load images
        backgroundImage.loadFromAssets("background.png")
        coverImage.loadFromAssets("the_rise_and_fall_of_a_midwest_princess.jpg")
        playButton.loadFromAssets("play.png")
        previousButton.loadFromAssets("previous.png")
        nextButton.loadFromAssets("next.png")
        QueueButton.loadFromAssets("playing_next.png")
        LyricsButton.loadFromAssets("song-lyrics.png")

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[MusicViewModel::class.java]

        // Observe playback state
        viewModel.isPlaying.observe(this) { playing ->
            if (playing) playButton.loadFromAssets("pause.png")
            else playButton.loadFromAssets("play.png")
        }

        // Observe current position
        viewModel.currentPosition.observe(this) { pos ->
            seekBar.progress = (pos * 100) / viewModel.totalDuration.value!!
            currentTimeText.text = formatTime(pos)
        }

        // Set total time
        totalTimeText.text = formatTime(viewModel.totalDuration.value ?: 0)

        // Start SeekBar updates
        handler.post(updateSeekBar)

        // --- Play/Pause button with click effect ---
        playButton.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (viewModel.isPlaying.value == true) {
                        playButton.loadFromAssets("pause_clicked.png")
                    } else {
                        playButton.loadFromAssets("play_clicked.png")
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (viewModel.isPlaying.value == true) {
                        viewModel.pause()
                    } else {
                        viewModel.play()
                    }
                }
            }
            true
        }

        // SeekBar listener
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val newPos = (viewModel.totalDuration.value!! * progress) / 100
                    viewModel.seekTo(newPos)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Volume controls
        volumeBar.max = 100
        volumeBar.progress = 70
        volumeBar.visibility = View.GONE
        volumeIcon.setOnClickListener {
            volumeBar.visibility = if (volumeBar.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }
        volumeBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) viewModel.setVolume(progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun formatTime(ms: Int): String {
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%d:%02d", minutes, seconds)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateSeekBar)
    }
}
