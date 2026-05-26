package com.practicum.playlistmaker.domain.search.interactor

import com.practicum.playlistmaker.domain.search.models.SearchResult
import kotlinx.coroutines.flow.Flow

interface TracksInteractor {
    fun searchTracks(expression: String): Flow<SearchResult>
}
