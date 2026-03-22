package com.practicum.playlistmaker.data.repository

import com.practicum.playlistmaker.data.NetworkClient
import com.practicum.playlistmaker.data.dto.SearchRequest
import com.practicum.playlistmaker.data.dto.SearchResponse
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.models.Track

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTracks(expression: String): List<Track> {
        val response = networkClient.doRequest(SearchRequest(expression))
        return if (response.resultCode == 200) {
            (response as SearchResponse).results.mapNotNull { dto ->
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
        } else {
            emptyList()
        }
    }
}
