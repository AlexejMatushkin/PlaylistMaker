package com.practicum.playlistmaker.domain.search.repository

import com.practicum.playlistmaker.domain.search.models.Track

interface SearchHistoryRepository {
    fun getHistory(): List<Track>
    fun addTrack(track: Track)
    fun clearHistory()
    fun hasHistory(): Boolean
}
