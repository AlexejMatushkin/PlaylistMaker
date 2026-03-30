package com.practicum.playlistmaker

import android.app.Application
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.domain.theme.interactor.ThemeManager

class PlaylistMakerApp : Application() {

    private lateinit var themeManager: ThemeManager

    override fun onCreate() {
        super.onCreate()
        Creator.init(this)

        themeManager = Creator.provideThemeManager()
        themeManager.applyCurrentTheme()
    }

}
