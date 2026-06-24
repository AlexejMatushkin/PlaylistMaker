package com.practicum.playlistmaker.domain.playlist.impl

import com.practicum.playlistmaker.data.db.AppDatabase
import com.practicum.playlistmaker.data.db.mapper.toDomain
import com.practicum.playlistmaker.data.db.mapper.toEntity
import com.practicum.playlistmaker.domain.playlist.model.Playlist
import com.practicum.playlistmaker.domain.playlist.repository.PlaylistRepository
import com.practicum.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase
) : PlaylistRepository {

    override suspend fun createPlaylist(playlist: Playlist) {
        appDatabase.playlistDao().insertPlaylist(playlist.toEntity())
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return appDatabase.playlistDao().getAllPlaylists().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun addTrackToPlaylist(playlist: Playlist, track: Track) {
        playlist.trackIds.add(track.trackId)
        val entity = playlist.toEntity().copy(count = playlist.trackIds.size)
        appDatabase.playlistDao().updatePlaylist(entity)
        appDatabase.trackInPlaylistDao().insertTrack(track.toTrackInPlaylistEntity())
    }
}

private fun Track.toTrackInPlaylistEntity() = com.practicum.playlistmaker.data.db.entity.TrackInPlaylistEntity(
    trackId = trackId,
    trackName = trackName,
    artistName = artistName,
    collectionName = collectionName,
    releaseDate = releaseDate,
    primaryGenreName = primaryGenreName,
    country = country,
    previewUrl = previewUrl,
    artworkUrl100 = artworkUrl100,
    trackTimeMillis = trackTimeMillis
)
