package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.ui.createPlaylist.CreatePlaylistViewModel
import com.practicum.playlistmaker.ui.createPlaylist.EditPlaylistViewModel
import com.practicum.playlistmaker.ui.mediaLibrary.viewModel.FavoriteTracksViewModel
import com.practicum.playlistmaker.ui.mediaLibrary.viewModel.MediaLibraryViewModel
import com.practicum.playlistmaker.ui.mediaLibrary.viewModel.PlaylistsViewModel
import com.practicum.playlistmaker.ui.playlist.PlaylistViewModel
import com.practicum.playlistmaker.ui.player.view_model.PlayerViewModel
import com.practicum.playlistmaker.ui.search.view_model.SearchViewModel
import com.practicum.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        SearchViewModel(
            tracksInteractor = get(),
            searchHistoryInteractor = get()
        )
    }

    viewModel {
        SettingsViewModel(
            getThemeSettingsInteractor = get(),
            themeManager = get(),
            sharingInteractor = get()
        )
    }

    viewModel {
        PlayerViewModel(get(), get(), get(), get())
    }

    viewModel { MediaLibraryViewModel() }
    viewModel { PlaylistsViewModel(get()) }
    viewModel { FavoriteTracksViewModel(get()) }
    viewModel { CreatePlaylistViewModel(get(), get()) }
    viewModel { EditPlaylistViewModel(get(), get()) }
    viewModel { PlaylistViewModel(get()) }
}
