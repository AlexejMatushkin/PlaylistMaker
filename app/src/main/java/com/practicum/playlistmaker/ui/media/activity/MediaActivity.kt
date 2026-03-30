package com.practicum.playlistmaker.ui.media.activity

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityMediaBinding
import com.practicum.playlistmaker.domain.search.models.Track
import com.practicum.playlistmaker.ui.media.view_model.MediaState
import com.practicum.playlistmaker.ui.media.view_model.MediaViewModel

class MediaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediaBinding
    private lateinit var viewModel: MediaViewModel
    private lateinit var track: Track

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        binding = ActivityMediaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_TRACK, Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_TRACK)
        } ?: run {
            finish()
            return
        }

        viewModel = ViewModelProvider(this)[MediaViewModel::class.java]
        viewModel.loadTrack(track)

        setupViews()
        setupBackButton()
        setupPlayButton()
        observeViewModel()
    }

    private fun setupViews() = binding.apply {
        trackName.text = track.trackName
        artistName.text = track.artistName
        genre.text = track.primaryGenreName ?: ""
        country.text = track.country ?: ""
        trackTime.text = track.getFormattedTime()
        trackProgress.text = getString(R.string.track_time_default)

        val placeholder = R.drawable.ic_music_note
        val highResUrl = track.getCoverArtwork()
        if (highResUrl != null) {
            Glide.with(this@MediaActivity)
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
        binding.backButton.setOnClickListener {
            viewModel.releasePlayer()
            finish()
        }
    }

    private fun setupPlayButton() {
        binding.playButton.setOnClickListener {
            viewModel.togglePlay()
        }
    }

    private fun observeViewModel() = binding.apply {
        viewModel.playerState.observe(this@MediaActivity) { state ->
            when (state) {
                MediaState.Playing -> {
                    playButton.setImageResource(R.drawable.ic_pause)
                    playButton.isEnabled = true
                }
                MediaState.Prepared, MediaState.Paused -> {
                    playButton.setImageResource(R.drawable.ic_play_circle)
                    playButton.isEnabled = true
                }
                MediaState.Default -> {
                    playButton.setImageResource(R.drawable.ic_play_circle)
                    playButton.isEnabled = true
                }
                MediaState.Error -> {
                    playButton.isEnabled = false
                }
            }
        }

        viewModel.currentPosition.observe(this@MediaActivity) { position ->
            trackProgress.text = viewModel.getFormattedTime(position)
        }
    }

    override fun onPause() {
        super.onPause()
        if (::viewModel.isInitialized) {
            viewModel.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::viewModel.isInitialized) {
            viewModel.releasePlayer()
        }
    }

    companion object {
        const val EXTRA_TRACK = "extra_track"
    }
}
