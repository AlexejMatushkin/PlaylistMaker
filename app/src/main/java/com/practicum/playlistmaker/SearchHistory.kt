package com.practicum.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(private val prefs: SharedPreferences) {
    private val gson = Gson()
    private val key = "search_history"
    private val maxSize = 10

    fun addTrack(track: Track) {
        val history = getHistory().toMutableList()

        history.removeAll { it.trackId == track.trackId }

        history.add(0, track)

        if (history.size > maxSize) {
            history.removeAt(history.size - 1)
        }

        saveHistory(history)
    }

    fun getHistory(): List<Track> {
        val json = prefs.getString(key, null) ?: return emptyList()
        val type = object : TypeToken<List<Track>>() {}.type
        return try {
            gson.fromJson<List<Track>>(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun clearHistory() {
        prefs.edit().remove(key).apply()
    }

    fun hasHistory(): Boolean {
        return getHistory().isNotEmpty()
    }

    private fun saveHistory(history: List<Track>) {
        val json = gson.toJson(history)
        prefs.edit().putString(key, json).apply()
    }
}
