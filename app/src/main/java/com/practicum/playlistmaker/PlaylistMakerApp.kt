package com.practicum.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.domain.api.SettingsInteractor

class PlaylistMakerApp : Application() {

    private lateinit var settingsInteractor: SettingsInteractor

    var darkTheme = false
        private set

    override fun onCreate() {
        super.onCreate()
        Creator.init(this)

        settingsInteractor = Creator.provideSettingsInteractor()

        val themeSettings = settingsInteractor.getThemeSettings()
        darkTheme = themeSettings.isDarkTheme
        applyTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        applyTheme(darkThemeEnabled)
        settingsInteractor.switchTheme(darkThemeEnabled)
    }

    private fun applyTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}
