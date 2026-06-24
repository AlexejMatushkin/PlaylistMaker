package com.practicum.playlistmaker.data.db.mapper

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.domain.playlist.model.Playlist

private val gson = Gson()

fun Playlist.toEntity(): PlaylistEntity = PlaylistEntity(
    id = id,
    name = name,
    description = description,
    imagePath = imagePath,
    trackIdsJson = gson.toJson(trackIds),
    count = trackIds.size
)

fun PlaylistEntity.toDomain(): Playlist = Playlist(
    id = id,
    name = name,
    description = description,
    imagePath = imagePath,
    trackIds = gson.fromJson(trackIdsJson, object : TypeToken<MutableList<Long>>() {}.type) ?: mutableListOf(),
    count = count
)
