package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.domain.favorite.impl.FavoriteTracksRepositoryImpl
import com.practicum.playlistmaker.domain.favorite.interactor.FavoriteInteractor
import com.practicum.playlistmaker.domain.favorite.interactor.FavoriteInteractorImpl
import com.practicum.playlistmaker.domain.favorite.repository.FavoriteTracksRepository
import com.practicum.playlistmaker.domain.search.impl.SearchHistoryInteractorImpl
import com.practicum.playlistmaker.domain.search.impl.TracksInteractorImpl
import com.practicum.playlistmaker.domain.search.interactor.SearchHistoryInteractor
import com.practicum.playlistmaker.domain.search.interactor.TracksInteractor
import com.practicum.playlistmaker.domain.sharing.impl.SharingInteractorImpl
import com.practicum.playlistmaker.domain.sharing.interactor.SharingInteractor
import com.practicum.playlistmaker.domain.theme.interactor.GetThemeSettingsInteractor
import com.practicum.playlistmaker.domain.theme.interactor.SwitchThemeInteractor
import org.koin.dsl.module

val repositoryModule = module {

    // ============= SEARCH =============
    factory<TracksInteractor> {
        TracksInteractorImpl(get())
    }

    factory<SearchHistoryInteractor> {
        SearchHistoryInteractorImpl(get())
    }

    // ============= SETTINGS =============
    factory { GetThemeSettingsInteractor(get()) }
    factory { SwitchThemeInteractor(get()) }

    // ============= SHARING =============
    factory<SharingInteractor> {
        SharingInteractorImpl(get())
    }

    single<FavoriteTracksRepository> { FavoriteTracksRepositoryImpl(get()) }
    factory<FavoriteInteractor> { FavoriteInteractorImpl(get()) }
}