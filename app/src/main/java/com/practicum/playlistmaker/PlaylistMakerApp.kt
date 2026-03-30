package com.practicum.playlistmaker

import android.app.Application
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.domain.theme.ThemeManager

class PlaylistMakerApp : Application() {

    private lateinit var themeManager: ThemeManager

    override fun onCreate() {
        super.onCreate()
        Creator.init(this)

        themeManager = Creator.provideThemeManager()
        themeManager.applyCurrentTheme()
    }

    fun switchTheme(isDark: Boolean) {
        themeManager.switchTheme(isDark)
    }
}
