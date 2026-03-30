package com.practicum.playlistmaker.creator

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker.data.NetworkClient
import com.practicum.playlistmaker.domain.search.impl.SearchHistoryInteractorImpl
import com.practicum.playlistmaker.domain.search.impl.TracksInteractorImpl
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.data.search.impl.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.data.search.repository.TracksRepositoryImpl
import com.practicum.playlistmaker.data.settings.repository.SettingsRepositoryImpl
import com.practicum.playlistmaker.data.sharing.impl.ExternalNavigatorImpl
import com.practicum.playlistmaker.data.theme.impl.ThemeManagerImpl
import com.practicum.playlistmaker.domain.sharing.ExternalNavigator
import com.practicum.playlistmaker.domain.search.interactor.SearchHistoryInteractor
import com.practicum.playlistmaker.domain.search.repository.SearchHistoryRepository
import com.practicum.playlistmaker.domain.settings.repository.SettingsRepository
import com.practicum.playlistmaker.domain.search.interactor.TracksInteractor
import com.practicum.playlistmaker.domain.search.repository.TracksRepository
import com.practicum.playlistmaker.domain.settings.impl.SettingsInteractorImpl
import com.practicum.playlistmaker.domain.settings.interactor.SettingsInteractor
import com.practicum.playlistmaker.domain.sharing.interactor.SharingInteractor
import com.practicum.playlistmaker.domain.sharing.impl.SharingInteractorImpl
import com.practicum.playlistmaker.domain.theme.GetThemeSettingsInteractor
import com.practicum.playlistmaker.domain.theme.SwitchThemeInteractor
import com.practicum.playlistmaker.domain.theme.ThemeManager

object Creator {
    private lateinit var applicationContext: Context
    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()

    fun init(context: Context) {
        applicationContext = context
        sharedPreferences = context.getSharedPreferences("playlist_maker", Context.MODE_PRIVATE)
    }

    fun provideThemeManager(): ThemeManager {
        return ThemeManagerImpl(
            getThemeSettingsInteractor = provideGetThemeSettingsInteractor(),
            switchThemeInteractor = provideSwitchThemeInteractor()
        )
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

    // ============= SETTINGS =============
    private fun provideSettingsRepository(): SettingsRepository {
        return SettingsRepositoryImpl(sharedPreferences)
    }
    // ============= Theme =============
    fun provideGetThemeSettingsInteractor(): GetThemeSettingsInteractor {
        return GetThemeSettingsInteractor(provideSettingsRepository())
    }

    fun provideSwitchThemeInteractor(): SwitchThemeInteractor {
        return SwitchThemeInteractor(provideSettingsRepository())
    }

    // ============= SHARING =============
    fun provideExternalNavigator(): ExternalNavigator {
        return ExternalNavigatorImpl(applicationContext)
    }

    fun provideSharingInteractor(): SharingInteractor {
        return SharingInteractorImpl(
            externalNavigator = provideExternalNavigator(),
            context = applicationContext
        )
    }
}
