package com.practicum.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class MediaActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_TRACK = "extra_track"
        private const val PROGRESS_UPDATE_INTERVAL_MS = 100L
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

    private lateinit var playButton: ImageButton
    private lateinit var trackProgress: TextView
    private lateinit var track: Track
    private lateinit var backButton: ImageButton

    private var mediaPlayer: MediaPlayer? = null
    private var playerState = STATE_DEFAULT
    private var playbackPosition = 0

    private val handler = Handler(Looper.getMainLooper())
    private val updateProgressRunnable = object : Runnable {
        override fun run() {
            updateProgress()
            handler.postDelayed(this, PROGRESS_UPDATE_INTERVAL_MS)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

       track = intent.getParcelableExtra<Track>(EXTRA_TRACK) ?: run {
            finish()
            return
        }

        initViews()
        setupViews()
        setupBackButton()
        setupPlayButton()

        preparePlayer()
    }


    private fun initViews() {
        playButton = findViewById(R.id.play_button)
        trackProgress = findViewById(R.id.track_progress)
        backButton = findViewById(R.id.back_button)
    }

    private fun setupViews() {
        val albumCover = findViewById<ImageView>(R.id.album_cover)
        val trackName = findViewById<TextView>(R.id.track_name)
        val artistName = findViewById<TextView>(R.id.artist_name)
        val albumLabel = findViewById<TextView>(R.id.album_label)
        val albumName = findViewById<TextView>(R.id.album_name)
        val yearLabel = findViewById<TextView>(R.id.year_label)
        val releaseYear = findViewById<TextView>(R.id.release_year)
        val genre = findViewById<TextView>(R.id.genre)
        val country = findViewById<TextView>(R.id.country)
        val trackTime = findViewById<TextView>(R.id.track_time)

        trackName.text = track.trackName
        artistName.text = track.artistName
        genre.text = track.primaryGenreName ?: ""
        country.text = track.country ?: ""
        trackTime.text = track.getFormattedTime()
        trackProgress.text = "00:00"

        val placeholder = R.drawable.ic_music_note
        val highResUrl = track.getCoverArtwork()
        if (highResUrl != null) {
            Glide.with(this)
                .load(highResUrl)
                .placeholder(placeholder)
                .error(placeholder)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(albumCover)
        } else {
            albumCover.setImageResource(placeholder)
        }

        if (!track.collectionName.isNullOrEmpty()) {
            albumName.text = track.collectionName
            albumLabel.isVisible = true
            albumName.isVisible = true
        } else {
            albumLabel.isVisible = false
            albumName.isVisible = false
        }

        val year = track.releaseDate?.takeIf { it.length >= 4 }?.substring(0, 4)
        if (year != null) {
            releaseYear.text = year
            yearLabel.isVisible = true
            releaseYear.isVisible = true
        } else {
            yearLabel.isVisible = false
            releaseYear.isVisible = false
        }
    }

    private fun setupBackButton() {
        backButton.setOnClickListener {
            releaseMediaPlayer()
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupPlayButton() {
        playButton.setOnClickListener {
            if (!track.previewUrl.isNullOrBlank()) {
                playbackControl()
            }
        }
    }

    private fun preparePlayer() {
        if (track.previewUrl.isNullOrBlank()) {
            playButton.isEnabled = false
            return
        }

        mediaPlayer = MediaPlayer().apply {
            setDataSource(track.previewUrl)
            prepareAsync()

            setOnPreparedListener {
                playButton.isEnabled = true
                playerState = STATE_PREPARED
                updatePlayButtonState()
            }

            setOnCompletionListener {
                playbackPosition = 0
                playerState = STATE_PREPARED
                updatePlayButtonState()
                handler.removeCallbacks(updateProgressRunnable)
                trackProgress.text = "00:00"
            }

            setOnErrorListener { _, what, extra ->
                playerState = STATE_DEFAULT
                playButton.isEnabled = false
                updatePlayButtonState()
                false
            }
        }
    }

    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
            STATE_DEFAULT -> {
                releaseMediaPlayer()
                preparePlayer()
            }
        }
    }

    private fun startPlayer() {
        mediaPlayer?.let {
            if (playerState == STATE_PREPARED || playerState == STATE_PAUSED) {
                if (playbackPosition > 0) {
                    it.seekTo(playbackPosition)
                }
                it.start()
                playerState = STATE_PLAYING
                updatePlayButtonState()
                handler.post(updateProgressRunnable)
            }
        }
    }

    private fun pausePlayer() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                playbackPosition = it.currentPosition
                it.pause()
                playerState = STATE_PAUSED
                updatePlayButtonState()
                handler.removeCallbacks(updateProgressRunnable)
            }
        }
    }

    private fun updatePlayButtonState() {
        when (playerState) {
            STATE_PLAYING -> {
                playButton.setImageResource(R.drawable.ic_pause)
                playButton.isEnabled = true
            }
            STATE_PREPARED, STATE_PAUSED -> {
                playButton.setImageResource(R.drawable.ic_play_circle)
                playButton.isEnabled = true
            }
            STATE_DEFAULT -> {
                playButton.setImageResource(R.drawable.ic_play_circle)
                playButton.isEnabled = false
            }
        }
    }

    private fun updateProgress() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                val currentPosition = it.currentPosition
                trackProgress.text = formatMillisToMinutesSeconds(currentPosition.toLong())
            }
        }
    }

    private fun formatMillisToMinutesSeconds(millis: Long): String {
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun releaseMediaPlayer() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }
        mediaPlayer = null
        playerState = STATE_DEFAULT
        playbackPosition = 0
        handler.removeCallbacks(updateProgressRunnable)
    }

    override fun onPause() {
        super.onPause()
        if (playerState == STATE_PLAYING) {
            pausePlayer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseMediaPlayer()
        handler.removeCallbacksAndMessages(null)
    }
}
