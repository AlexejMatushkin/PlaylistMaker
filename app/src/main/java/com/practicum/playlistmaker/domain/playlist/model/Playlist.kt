package com.practicum.playlistmaker.domain.playlist.model

data class Playlist(
    val id: Int = 0,
    val name: String,
    val description: String,
    val imagePath: String,
    val trackIds: MutableList<Long> = mutableListOf(),
    val count: Int = 0
)
