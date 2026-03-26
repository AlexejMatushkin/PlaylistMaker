package com.practicum.playlistmaker.presentation.viewmodel

import com.practicum.playlistmaker.domain.models.Track

sealed class SearchHistoryState {
    data class History(val tracks: List<Track>) : SearchHistoryState()
    object Empty : SearchHistoryState()
}