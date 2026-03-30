package com.practicum.playlistmaker.domain.search.interactor

import com.practicum.playlistmaker.domain.search.models.SearchResult

interface TracksInteractor {
    fun searchTracks(expression: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(result: SearchResult)
    }
}