package com.practicum.playlistmaker.domain.settings.interactor

import com.practicum.playlistmaker.domain.theme.ThemeSettings

interface SettingsInteractor {
    fun getThemeSettings(): ThemeSettings
    fun switchTheme(isDark: Boolean)
}