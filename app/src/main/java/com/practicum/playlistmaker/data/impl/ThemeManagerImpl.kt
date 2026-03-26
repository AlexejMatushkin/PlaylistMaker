package com.practicum.playlistmaker.data.impl

import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.domain.api.SettingsInteractor
import com.practicum.playlistmaker.domain.api.ThemeManager

class ThemeManagerImpl(
    private val settingsInteractor: SettingsInteractor
) : ThemeManager {

    override fun switchTheme(isDark: Boolean) {
        settingsInteractor.switchTheme(isDark)
        applyTheme(isDark)
    }

    override fun getCurrentTheme(): Boolean {
        return settingsInteractor.getThemeSettings().isDarkTheme
    }

    override fun applyCurrentTheme() {
        val isDark = getCurrentTheme()
        applyTheme(isDark)
    }

    private fun applyTheme(isDark: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}