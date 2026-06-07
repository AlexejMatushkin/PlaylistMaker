package com.practicum.playlistmaker.domain.favorite.interactor

import com.practicum.playlistmaker.domain.favorite.repository.FavoriteTracksRepository
import com.practicum.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.flow.Flow

class FavoriteInteractorImpl(
    private val repository: FavoriteTracksRepository
) : FavoriteInteractor {
    override suspend fun addToFavorites(track: Track) = repository.addToFavorites(track)
    override suspend fun removeFromFavorites(track: Track) = repository.removeFromFavorites(track)
    override fun getAllFavorites(): Flow<List<Track>> = repository.getAllFavorites()
    override suspend fun isFavorite(trackId: Long): Boolean = repository.isFavorite(trackId)
}