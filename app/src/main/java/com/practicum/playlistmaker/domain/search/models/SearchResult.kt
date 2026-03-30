package com.practicum.playlistmaker.domain.search.models

sealed interface SearchResult {
    data class Success(val tracks: List<Track>) : SearchResult
    object Error : SearchResult
}