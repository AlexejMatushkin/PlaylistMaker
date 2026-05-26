package com.practicum.playlistmaker.data.search.repository

import com.practicum.playlistmaker.data.NetworkClient
import com.practicum.playlistmaker.data.search.dto.SearchRequest
import com.practicum.playlistmaker.data.search.dto.SearchResponse
import com.practicum.playlistmaker.domain.search.models.SearchResult
import com.practicum.playlistmaker.domain.search.models.Track
import com.practicum.playlistmaker.domain.search.repository.TracksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTracks(expression: String): Flow<SearchResult> = flow {
        val response = networkClient.doRequest(SearchRequest(expression))
        when (response.resultCode) {
            200 -> {
                val tracks = (response as SearchResponse).results.mapNotNull { dto ->
                    if (dto.trackId != null && !dto.trackName.isNullOrEmpty() && !dto.artistName.isNullOrEmpty()) {
                        Track(
                            trackId = dto.trackId,
                            trackName = dto.trackName,
                            artistName = dto.artistName,
                            collectionName = dto.collectionName,
                            releaseDate = dto.releaseDate,
                            primaryGenreName = dto.primaryGenreName,
                            country = dto.country,
                            previewUrl = dto.previewUrl,
                            artworkUrl100 = dto.artworkUrl100,
                            trackTimeMillis = dto.trackTimeMillis
                        )
                    } else {
                        null
                    }
                }
                emit(SearchResult.Success(tracks))
            }
            -1, -2 -> emit(SearchResult.Error)
            else -> emit(SearchResult.Error)
        }
    }.flowOn(Dispatchers.IO)
}
