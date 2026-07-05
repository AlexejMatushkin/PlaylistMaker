package com.practicum.playlistmaker.data.db.mapper

import com.practicum.playlistmaker.data.db.entity.TrackEntity
import com.practicum.playlistmaker.data.db.entity.TrackInPlaylistEntity
import com.practicum.playlistmaker.domain.search.models.Track

fun Track.toEntity(): TrackEntity = TrackEntity(
    trackId = trackId,
    createdAt = System.currentTimeMillis(),
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

fun TrackEntity.toDomain(): Track = Track(
    trackId = trackId,
    trackName = trackName,
    artistName = artistName,
    collectionName = collectionName,
    releaseDate = releaseDate,
    primaryGenreName = primaryGenreName,
    country = country,
    previewUrl = previewUrl,
    artworkUrl100 = artworkUrl100,
    trackTimeMillis = trackTimeMillis,
    isFavorite = true
)

fun TrackInPlaylistEntity.toTrackDomain(): Track = Track(
    trackId = trackId,
    trackName = trackName,
    artistName = artistName,
    collectionName = collectionName,
    releaseDate = releaseDate,
    primaryGenreName = primaryGenreName,
    country = country,
    previewUrl = previewUrl,
    artworkUrl100 = artworkUrl100,
    trackTimeMillis = trackTimeMillis,
    isFavorite = false
)