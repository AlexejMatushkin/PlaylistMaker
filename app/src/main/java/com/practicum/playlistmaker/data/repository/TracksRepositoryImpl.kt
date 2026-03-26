package com.practicum.playlistmaker.data.repository

import com.practicum.playlistmaker.data.NetworkClient
import com.practicum.playlistmaker.data.dto.SearchRequest
import com.practicum.playlistmaker.data.dto.SearchResponse
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.models.SearchResult
import com.practicum.playlistmaker.domain.models.Track

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTracks(expression: String): SearchResult {
        val response = networkClient.doRequest(SearchRequest(expression))

        return when (response.resultCode) {
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
                SearchResult.Success(tracks)
            }
            -1, -2 -> {
                // Ошибка сети или другая ошибка
                SearchResult.Error
            }
            else -> {
                // Другие коды ответа (400, 404, 500 и т.д.)
                SearchResult.Error
            }
        }
    }
}
