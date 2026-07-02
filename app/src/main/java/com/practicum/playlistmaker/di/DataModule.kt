package com.practicum.playlistmaker.di

import android.content.Context
import android.media.MediaPlayer
import androidx.room.Room
import com.google.gson.Gson
import com.practicum.playlistmaker.data.NetworkClient
import com.practicum.playlistmaker.data.db.AppDatabase
import com.practicum.playlistmaker.data.player.repository.MediaPlayerRepositoryImpl
import com.practicum.playlistmaker.data.network.ITunesApiService
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.data.search.impl.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.data.search.repository.TracksRepositoryImpl
import com.practicum.playlistmaker.data.settings.repository.SettingsRepositoryImpl
import com.practicum.playlistmaker.data.sharing.impl.ExternalNavigatorImpl
import com.practicum.playlistmaker.data.theme.impl.ThemeManagerImpl
import com.practicum.playlistmaker.domain.favorite.impl.FavoriteTracksRepositoryImpl
import com.practicum.playlistmaker.domain.favorite.repository.FavoriteTracksRepository
import com.practicum.playlistmaker.domain.media.MediaPlayerRepository
import com.practicum.playlistmaker.data.playlist.PlaylistImageRepositoryImpl
import com.practicum.playlistmaker.domain.playlist.impl.PlaylistRepositoryImpl
import com.practicum.playlistmaker.domain.playlist.repository.PlaylistImageRepository
import com.practicum.playlistmaker.domain.playlist.repository.PlaylistRepository
import com.practicum.playlistmaker.domain.search.repository.SearchHistoryRepository
import com.practicum.playlistmaker.domain.search.repository.TracksRepository
import com.practicum.playlistmaker.domain.settings.repository.SettingsRepository
import com.practicum.playlistmaker.domain.sharing.ExternalNavigator
import com.practicum.playlistmaker.domain.theme.interactor.ThemeManager
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "https://itunes.apple.com"

val dataModule = module {

    // ============= NETWORK =============
    single {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApiService::class.java)
    }

    single<NetworkClient> {
        RetrofitNetworkClient(get())
    }

    // ============= STORAGE =============
    single {
        androidContext().getSharedPreferences("playlist_maker", Context.MODE_PRIVATE)
    }

    factory { Gson() }

    // ============= REPOSITORIES =============
    single<TracksRepository> {
        TracksRepositoryImpl(get(), get())
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get(), get(), get())
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

    // ============= MEDIA PLAYER =============
    factory<() -> MediaPlayer> { { MediaPlayer() } }

    factory<MediaPlayerRepository> {
        MediaPlayerRepositoryImpl(
            mediaPlayerFactory = get()
        )
    }

    // ============= ROOM DATABASE =============
    single {
        Room.databaseBuilder(
            androidApplication(),
            AppDatabase::class.java,
            "playlist_maker.db"
        ).fallbackToDestructiveMigration(false).build()
    }

    single { get<AppDatabase>().favoriteTracksDao() }
    single { get<AppDatabase>().playlistDao() }
    single { get<AppDatabase>().trackInPlaylistDao() }

    single<PlaylistRepository> {
 PlaylistRepositoryImpl(
        playlistDao = get(),
        trackInPlaylistDao = get()
    )
}

    single<PlaylistImageRepository> {
        PlaylistImageRepositoryImpl(
            contentResolver = androidContext().contentResolver,
            filesDir = androidContext().getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)!!
        )
    }
}
