package com.practicum.playlistmaker.domain.favorite.impl

import com.practicum.playlistmaker.data.db.dao.FavoriteTracksDao
import com.practicum.playlistmaker.data.db.mapper.toDomain
import com.practicum.playlistmaker.data.db.mapper.toEntity
import com.practicum.playlistmaker.domain.favorite.repository.FavoriteTracksRepository
import com.practicum.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteTracksRepositoryImpl(
    private val dao: FavoriteTracksDao
) : FavoriteTracksRepository {

    override suspend fun addToFavorites(track: Track) {
        dao.addToFavorites(track.toEntity())
    }

    override suspend fun removeFromFavorites(track: Track) {
        dao.removeFromFavorites(track.trackId)
    }

    override fun getAllFavorites(): Flow<List<Track>> {
        return dao.getAllFavorites().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun isFavorite(trackId: Long): Boolean {
        val ids = dao.getFavoriteIds()
        return ids.contains(trackId)
    }
}