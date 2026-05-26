package com.practicum.playlistmaker.domain.search.impl

import com.practicum.playlistmaker.domain.search.repository.TracksRepository
import com.practicum.playlistmaker.domain.search.interactor.TracksInteractor
import com.practicum.playlistmaker.domain.search.models.SearchResult
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.Executors

class TracksInteractorImpl(
    private val repository: TracksRepository
) : TracksInteractor {

    override fun searchTracks(expression: String): Flow<SearchResult> =
        repository.searchTracks(expression)
}
