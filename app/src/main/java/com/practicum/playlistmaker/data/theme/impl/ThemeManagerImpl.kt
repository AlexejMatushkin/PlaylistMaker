package com.practicum.playlistmaker.data.theme.impl

import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.domain.theme.GetThemeSettingsInteractor
import com.practicum.playlistmaker.domain.theme.SwitchThemeInteractor
import com.practicum.playlistmaker.domain.theme.ThemeManager

class ThemeManagerImpl(
    private val getThemeSettingsInteractor: GetThemeSettingsInteractor,
    private val switchThemeInteractor: SwitchThemeInteractor
) : ThemeManager {

    override fun switchTheme(isDark: Boolean) {
        switchThemeInteractor(isDark)
        applyTheme(isDark)
    }

    override fun getCurrentTheme(): Boolean {
        return getThemeSettingsInteractor().isDarkTheme
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
