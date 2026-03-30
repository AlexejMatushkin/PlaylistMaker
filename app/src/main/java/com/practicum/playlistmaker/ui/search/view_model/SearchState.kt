package com.practicum.playlistmaker.ui.search.view_model

import com.practicum.playlistmaker.domain.search.models.Track

sealed class SearchState {
    object Empty : SearchState()
    object Loading : SearchState()
    data class Success(val tracks: List<Track>) : SearchState()
    object Error : SearchState()
    object NoResults : SearchState()
}