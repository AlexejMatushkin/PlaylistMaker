package com.practicum.playlistmaker.ui.search.view_model

import com.practicum.playlistmaker.domain.search.models.Track

sealed class SearchHistoryState {
    data class History(val tracks: List<Track>) : SearchHistoryState()
    object Empty : SearchHistoryState()
}