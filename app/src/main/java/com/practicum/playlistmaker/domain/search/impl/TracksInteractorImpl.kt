package com.practicum.playlistmaker.domain.search.impl

import com.practicum.playlistmaker.domain.search.repository.TracksRepository
import com.practicum.playlistmaker.domain.search.interactor.TracksInteractor
import java.util.concurrent.Executors

class TracksInteractorImpl(
    private val repository: TracksRepository
) : TracksInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String, consumer: TracksInteractor.TracksConsumer) {
        executor.execute {
            val result = repository.searchTracks(expression)
            consumer.consume(result)
        }
    }
}