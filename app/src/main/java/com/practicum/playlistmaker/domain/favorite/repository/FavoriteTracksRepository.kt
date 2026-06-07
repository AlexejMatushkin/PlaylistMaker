package com.practicum.playlistmaker.domain.favorite.repository

import com.practicum.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksRepository {
    suspend fun addToFavorites(track: Track)
    suspend fun removeFromFavorites(track: Track)
    fun getAllFavorites(): Flow<List<Track>>
    suspend fun isFavorite(trackId: Long): Boolean
}
