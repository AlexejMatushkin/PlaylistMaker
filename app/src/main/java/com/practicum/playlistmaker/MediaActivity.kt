package com.practicum.playlistmaker

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import java.text.SimpleDateFormat
import java.util.Locale

class MediaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)

        if (intent.getStringExtra("track_name").isNullOrBlank()) {
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

        val trackNameStr = intent.getStringExtra("track_name") ?: ""
        val artistNameStr = intent.getStringExtra("artist_name") ?: ""
        val collectionName = intent.getStringExtra("collection_name")
        val releaseDateStr = intent.getStringExtra("release_date")
        val primaryGenreName = intent.getStringExtra("genre") ?: ""
        val countryStr = intent.getStringExtra("country") ?: ""
        val artworkUrl100 = intent.getStringExtra("artwork_url")
        val trackTimeMillis = intent.getLongExtra("track_time", -1L).takeIf { it != -1L }

        trackName.text = trackNameStr
        artistName.text = artistNameStr
        genre.text = primaryGenreName
        country.text = countryStr

        trackTime.text = trackTimeMillis?.let {
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(it)
        } ?: ""

        if (!collectionName.isNullOrEmpty()) {
            albumName.text = collectionName
            albumLabel.isVisible = true
            albumName.isVisible = true
        } else {
            albumLabel.isVisible = false
            albumName.isVisible = false
        }

        val releaseYearStr = releaseDateStr?.takeIf { it.length >= 4 }?.substring(0, 4)
        if (releaseYearStr != null) {
            releaseYear.text = releaseYearStr
            yearLabel.isVisible = true
            releaseYear.isVisible = true
        } else {
            yearLabel.isVisible = false
            releaseYear.isVisible = false
        }

        val placeholder = R.drawable.ic_music_note
        if (!artworkUrl100.isNullOrEmpty()) {
            val highResUrl = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
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
