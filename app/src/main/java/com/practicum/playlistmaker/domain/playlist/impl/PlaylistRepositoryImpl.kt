package com.practicum.playlistmaker.domain.playlist.impl

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.data.db.dao.PlaylistDao
import com.practicum.playlistmaker.data.db.dao.TrackInPlaylistDao
import com.practicum.playlistmaker.data.db.mapper.toDomain
import com.practicum.playlistmaker.data.db.mapper.toEntity
import com.practicum.playlistmaker.data.db.mapper.toTrackDomain
import com.practicum.playlistmaker.domain.playlist.model.Playlist
import com.practicum.playlistmaker.domain.playlist.repository.PlaylistRepository
import com.practicum.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val trackInPlaylistDao: TrackInPlaylistDao
) : PlaylistRepository {

    override suspend fun createPlaylist(playlist: Playlist) {
        playlistDao.insertPlaylist(playlist.toEntity())
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getAllPlaylists().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getPlaylistById(id: Int): Flow<Playlist?> {
        return playlistDao.getPlaylistById(id).map { it?.toDomain() }
    }

    override fun getTracksByIds(ids: List<Long>): Flow<List<Track>> {
        return trackInPlaylistDao.getAllTracks().map { list ->
            list.filter { it.trackId in ids }.map { it.toTrackDomain() }
        }
    }

    override suspend fun addTrackToPlaylist(playlist: Playlist, track: Track) {
        val updatedTrackIds = playlist.trackIds + track.trackId
        val updatedPlaylist = playlist.copy(trackIds = updatedTrackIds)
        val entity = updatedPlaylist.toEntity().copy(count = updatedTrackIds.size)
        playlistDao.updatePlaylist(entity)
        trackInPlaylistDao.insertTrack(track.toTrackInPlaylistEntity())
    }

    override suspend fun removeTrackFromPlaylist(playlist: Playlist, trackId: Long) {
        val updatedTrackIds = playlist.trackIds - trackId
        val updatedPlaylist = playlist.copy(trackIds = updatedTrackIds)
        val entity = updatedPlaylist.toEntity().copy(count = updatedTrackIds.size)
        playlistDao.updatePlaylist(entity)
        cleanupOrphanedTrack(trackId)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistDao.updatePlaylist(playlist.toEntity())
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        playlistDao.deletePlaylistById(playlist.id)
        playlist.trackIds.forEach { cleanupOrphanedTrack(it) }
    }

    private suspend fun cleanupOrphanedTrack(trackId: Long) {
        val allPlaylists = playlistDao.getAllPlaylists().first()
        val gson = Gson()
        val isInAnyPlaylist = allPlaylists.any { playlistEntity ->
            val trackIds: List<Long> = gson.fromJson(
                playlistEntity.trackIdsJson,
                object : TypeToken<List<Long>>() {}.type
            ) ?: emptyList()
            trackIds.contains(trackId)
        }
        if (!isInAnyPlaylist) {
            trackInPlaylistDao.deleteTrack(trackId)
        }
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
