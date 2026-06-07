package com.practicum.playlistmaker.domain.search.repository

import com.practicum.playlistmaker.domain.search.models.Track

interface SearchHistoryRepository {
    suspend fun getHistory(): List<Track>
    suspend fun addTrack(track: Track)
    suspend fun clearHistory()
    suspend fun hasHistory(): Boolean
}
