package com.practicum.playlistmaker.domain.search.interactor

import com.practicum.playlistmaker.domain.search.models.Track

interface SearchHistoryInteractor {
    suspend fun getHistory(): List<Track>
    suspend fun addTrack(track: Track)
    suspend fun clearHistory()
    suspend fun hasHistory(): Boolean
}
