package com.practicum.playlistmaker

import android.app.Application
import com.practicum.playlistmaker.di.dataModule
import com.practicum.playlistmaker.di.repositoryModule
import com.practicum.playlistmaker.di.viewModelModule
import com.practicum.playlistmaker.domain.theme.interactor.ThemeManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin

class PlaylistMakerApp : Application(), KoinComponent {

    private val themeManager: ThemeManager by inject()

    override fun onCreate() {
        super.onCreate()

        // Инициализируем Koin
        startKoin {
            androidContext(this@PlaylistMakerApp)
            modules(
                dataModule,
                repositoryModule,
                viewModelModule
            )
        }

        // Применяем тему
        themeManager.applyCurrentTheme()
    }
}
