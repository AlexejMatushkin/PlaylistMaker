package com.practicum.playlistmaker.di

import android.content.Context
import com.google.gson.Gson
import com.practicum.playlistmaker.data.NetworkClient
import com.practicum.playlistmaker.data.network.ITunesApiService
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.data.search.impl.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.data.search.repository.TracksRepositoryImpl
import com.practicum.playlistmaker.data.settings.repository.SettingsRepositoryImpl
import com.practicum.playlistmaker.data.sharing.impl.ExternalNavigatorImpl
import com.practicum.playlistmaker.data.theme.impl.ThemeManagerImpl
import com.practicum.playlistmaker.domain.search.repository.SearchHistoryRepository
import com.practicum.playlistmaker.domain.search.repository.TracksRepository
import com.practicum.playlistmaker.domain.settings.repository.SettingsRepository
import com.practicum.playlistmaker.domain.sharing.ExternalNavigator
import com.practicum.playlistmaker.domain.theme.interactor.ThemeManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    // ============= NETWORK =============
    single {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApiService::class.java)
    }

    single<NetworkClient> {
        RetrofitNetworkClient()
    }

    // ============= STORAGE =============
    single {
        androidContext().getSharedPreferences("playlist_maker", Context.MODE_PRIVATE)
    }

    factory { Gson() }

    // ============= REPOSITORIES =============
    single<TracksRepository> {
        TracksRepositoryImpl(get())
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get(), get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

    // ============= EXTERNAL NAVIGATOR =============
    single<ExternalNavigator> {
        ExternalNavigatorImpl(androidContext())
    }

    // ============= THEME MANAGER =============
    single<ThemeManager> {
        ThemeManagerImpl(
            getThemeSettingsInteractor = get(),
            switchThemeInteractor = get()
        )
    }
}