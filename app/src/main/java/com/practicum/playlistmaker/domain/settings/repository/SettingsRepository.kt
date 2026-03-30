package com.practicum.playlistmaker.domain.settings.repository

import com.practicum.playlistmaker.domain.theme.models.ThemeSettings

interface SettingsRepository {
    fun getThemeSettings(): ThemeSettings
    fun updateThemeSettings(settings: ThemeSettings)
}
