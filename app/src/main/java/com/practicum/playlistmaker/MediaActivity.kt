package com.practicum.playlistmaker

import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class MediaActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_TRACK = "extra_track"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val track = intent.getParcelableExtra<Track>(EXTRA_TRACK) ?: run {
            finish()
            return
        }

        val backButton = findViewById<ImageButton>(R.id.back_button)
        val albumCover = findViewById<ImageView>(R.id.album_cover)
        val trackName = findViewById<TextView>(R.id.track_name)
        val artistName = findViewById<TextView>(R.id.artist_name)
        val trackProgress = findViewById<TextView>(R.id.track_progress)
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

        val cornerRadius = DimensionUtils.pxToDp(
            8f,
            this
        )

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

        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}
