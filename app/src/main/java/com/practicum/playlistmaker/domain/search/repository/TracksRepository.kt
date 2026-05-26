package com.practicum.playlistmaker.domain.search.repository

import com.practicum.playlistmaker.domain.search.models.SearchResult
import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    fun searchTracks(expression: String): Flow<SearchResult>
}
