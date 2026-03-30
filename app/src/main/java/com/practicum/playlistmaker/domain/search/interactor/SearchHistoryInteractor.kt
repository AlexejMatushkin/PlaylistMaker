package com.practicum.playlistmaker.domain.search.interactor

import com.practicum.playlistmaker.domain.search.models.Track

interface SearchHistoryInteractor {
    fun getHistory(): List<Track>
    fun addTrack(track: Track)
    fun clearHistory()
    fun hasHistory(): Boolean
}