package com.practicum.playlistmaker.domain.search.repository

import com.practicum.playlistmaker.domain.search.models.SearchResult

interface TracksRepository {
    fun searchTracks(expression: String): SearchResult
}
