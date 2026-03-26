package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.SearchResult

interface TracksRepository {
    fun searchTracks(expression: String): SearchResult
}
