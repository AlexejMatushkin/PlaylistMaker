package com.practicum.playlistmaker

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker.data.NetworkClient
import com.practicum.playlistmaker.data.impl.SettingsInteractorImpl
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.data.repository.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.data.repository.SettingsRepositoryImpl
import com.practicum.playlistmaker.data.repository.TracksRepositoryImpl
import com.practicum.playlistmaker.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.domain.api.SettingsInteractor
import com.practicum.playlistmaker.domain.api.SettingsRepository
import com.practicum.playlistmaker.domain.api.ThemeManager
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.data.impl.SearchHistoryInteractorImpl
import com.practicum.playlistmaker.data.impl.ThemeManagerImpl
import com.practicum.playlistmaker.data.impl.TracksInteractorImpl

object Creator {
    private lateinit var applicationContext: Context
    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()

    fun init(context: Context) {
        applicationContext = context
        sharedPreferences = context.getSharedPreferences("playlist_maker", Context.MODE_PRIVATE)
    }

    // ============= NETWORK =============
    private fun getNetworkClient(): NetworkClient {
        return RetrofitNetworkClient()
    }

    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(getNetworkClient())
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    // ============= SEARCH HISTORY =============
    private fun getSearchHistoryRepository(): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(sharedPreferences, gson)
    }

    fun provideSearchHistoryInteractor(): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(getSearchHistoryRepository())
    }

    // ============= SETTINGS & THEME =============
    private fun getSettingsRepository(): SettingsRepository {
        return SettingsRepositoryImpl(sharedPreferences)
    }

    fun provideSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository())
    }

    fun provideThemeManager(): ThemeManager {
        return ThemeManagerImpl(provideSettingsInteractor())
    }
}
