package com.practicum.playlistmaker.domain.playlist.interactor

import com.practicum.playlistmaker.domain.playlist.model.Playlist
import com.practicum.playlistmaker.domain.playlist.repository.PlaylistRepository
import com.practicum.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val repository: PlaylistRepository
) : PlaylistInteractor {

    override suspend fun createPlaylist(playlist: Playlist) {
        repository.createPlaylist(playlist)
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return repository.getAllPlaylists()
    }

    override fun getPlaylistById(id: Int): Flow<Playlist?> {
        return repository.getPlaylistById(id)
    }

    override fun getTracksByIds(ids: List<Long>): Flow<List<Track>> {
        return repository.getTracksByIds(ids)
    }

    override suspend fun addTrackToPlaylist(playlist: Playlist, track: Track) {
        repository.addTrackToPlaylist(playlist, track)
    }

    override suspend fun removeTrackFromPlaylist(playlist: Playlist, trackId: Long) {
        repository.removeTrackFromPlaylist(playlist, trackId)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        repository.updatePlaylist(playlist)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        repository.deletePlaylist(playlist)
    }
}
