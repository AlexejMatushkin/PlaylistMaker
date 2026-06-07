package com.practicum.playlistmaker.domain.search.impl

import com.practicum.playlistmaker.domain.search.repository.SearchHistoryRepository
import com.practicum.playlistmaker.domain.search.models.Track
import com.practicum.playlistmaker.domain.search.interactor.SearchHistoryInteractor

class SearchHistoryInteractorImpl(
    private val repository: SearchHistoryRepository
) : SearchHistoryInteractor {

    override suspend fun getHistory(): List<Track> = repository.getHistory()
    override suspend fun addTrack(track: Track) = repository.addTrack(track)
    override suspend fun clearHistory() = repository.clearHistory()
    override suspend fun hasHistory(): Boolean = repository.hasHistory()
}
