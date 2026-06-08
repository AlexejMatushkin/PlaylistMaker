package com.practicum.playlistmaker.data.search.impl

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.practicum.playlistmaker.data.db.dao.FavoriteTracksDao
import com.practicum.playlistmaker.domain.search.repository.SearchHistoryRepository
import com.practicum.playlistmaker.domain.search.models.Track

class SearchHistoryRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson,
    private val favoriteTracksDao: FavoriteTracksDao
) : SearchHistoryRepository {
    override suspend fun getHistory(): List<Track> {
        val json = sharedPreferences.getString(HISTORY_KEY, null) ?: return emptyList()
        val tracks = try {
            gson.fromJson(json, Array<Track>::class.java).toList()
        } catch (_: Exception) {
            emptyList()
        }
        val favoriteIds = favoriteTracksDao.getFavoriteIds()
        return tracks.map { track ->
            track.copy(isFavorite = favoriteIds.contains(track.trackId))
        }
    }

    override suspend fun addTrack(track: Track) {
        val history = getHistory().toMutableList()
        history.removeAll { it.trackId == track.trackId }
        history.add(0, track)

        val updatedHistory = if (history.size > MAX_HISTORY_SIZE) {
            history.subList(0, MAX_HISTORY_SIZE)
        } else {
            history
        }

        saveHistory(updatedHistory)
    }

    override suspend fun clearHistory() {
        sharedPreferences.edit { remove(HISTORY_KEY) }
    }

    override suspend fun hasHistory(): Boolean = getHistory().isNotEmpty()

    private fun saveHistory(history: List<Track>) {
        val json = gson.toJson(history)
        sharedPreferences.edit { putString(HISTORY_KEY, json) }
    }

    companion object {
        private const val HISTORY_KEY = "search_history"
        private const val MAX_HISTORY_SIZE = 10
    }
}