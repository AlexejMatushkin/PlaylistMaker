package com.practicum.playlistmaker.domain.playlist.repository

import com.practicum.playlistmaker.domain.playlist.model.Playlist
import com.practicum.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun createPlaylist(playlist: Playlist)
    fun getAllPlaylists(): Flow<List<Playlist>>
    fun getPlaylistById(id: Int): Flow<Playlist?>
    fun getTracksByIds(ids: List<Long>): Flow<List<Track>>
    suspend fun addTrackToPlaylist(playlist: Playlist, track: Track)
    suspend fun removeTrackFromPlaylist(playlist: Playlist, trackId: Long)
    suspend fun updatePlaylist(playlist: Playlist)
    suspend fun deletePlaylist(playlist: Playlist)
}
