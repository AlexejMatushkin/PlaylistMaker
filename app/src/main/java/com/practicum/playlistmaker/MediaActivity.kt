package com.practicum.playlistmaker

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import java.text.SimpleDateFormat
import java.util.Locale

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

        val backButton = findViewById<ImageButton>(R.id.backButton)
        val albumCover = findViewById<ImageView>(R.id.albumCover)
        val trackName = findViewById<TextView>(R.id.trackName)
        val artistName = findViewById<TextView>(R.id.artistName)
        val albumLabel = findViewById<TextView>(R.id.albumLabel)
        val albumName = findViewById<TextView>(R.id.albumName)
        val yearLabel = findViewById<TextView>(R.id.yearLabel)
        val releaseYear = findViewById<TextView>(R.id.releaseYear)
        val genre = findViewById<TextView>(R.id.genre)
        val country = findViewById<TextView>(R.id.country)
        val trackTime = findViewById<TextView>(R.id.trackTime)

        trackName.text = track.trackName
        artistName.text = track.artistName
        genre.text = track.primaryGenreName ?: ""
        country.text = track.country ?: ""
        trackTime.text = track.getFormattedTime()


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

        val placeholder = R.drawable.ic_music_note
        val highResUrl = track.getCoverArtwork()
        if (highResUrl != null) {
            Glide.with(this)
                .load(highResUrl)
                .placeholder(placeholder)
                .error(placeholder)
                .transition(DrawableTransitionOptions.withCrossFade())
                .centerCrop()
                .into(albumCover)
        } else {
            albumCover.setImageResource(placeholder)
        }

        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}
