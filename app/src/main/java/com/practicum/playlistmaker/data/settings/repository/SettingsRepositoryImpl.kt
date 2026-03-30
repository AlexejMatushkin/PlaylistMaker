package com.practicum.playlistmaker.data.settings.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import com.practicum.playlistmaker.domain.settings.repository.SettingsRepository
import com.practicum.playlistmaker.domain.theme.ThemeSettings

class SettingsRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : SettingsRepository {

    companion object {
        private const val THEME_KEY = "dark_theme"
    }

    override fun getThemeSettings(): ThemeSettings {
        val isDark = sharedPreferences.getBoolean(THEME_KEY, false)
        return ThemeSettings(isDark)
    }

    override fun updateThemeSettings(settings: ThemeSettings) {
        sharedPreferences.edit {
            putBoolean(THEME_KEY, settings.isDarkTheme)
        }
    }
}