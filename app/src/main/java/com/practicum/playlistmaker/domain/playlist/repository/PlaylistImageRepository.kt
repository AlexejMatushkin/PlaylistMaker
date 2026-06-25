package com.practicum.playlistmaker.domain.playlist.repository

interface PlaylistImageRepository {
    fun saveImageToPrivateStorage(uriString: String): String
}
