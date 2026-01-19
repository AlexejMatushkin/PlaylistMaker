package com.practicum.playlistmaker

import java.text.SimpleDateFormat
import java.util.Locale

data class Track(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long?,
    val artworkUrl100: String?,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?
){
    fun getFormattedTime(): String {
        return trackTimeMillis?.let {
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(it)
        } ?: ""
    }
}
