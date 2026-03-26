package com.practicum.playlistmaker.presentation.viewmodel

import com.practicum.playlistmaker.domain.models.Track

sealed class SearchState {
    object Empty : SearchState()
    object Loading : SearchState()
    data class Success(val tracks: List<Track>) : SearchState()
    object Error : SearchState()
    object NoResults : SearchState()
}