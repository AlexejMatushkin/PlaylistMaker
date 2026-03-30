package com.practicum.playlistmaker.domain.settings.impl

import com.practicum.playlistmaker.domain.settings.interactor.SettingsInteractor
import com.practicum.playlistmaker.domain.settings.repository.SettingsRepository
import com.practicum.playlistmaker.domain.theme.ThemeSettings

class SettingsInteractorImpl(
    private val settingsRepository: SettingsRepository
) : SettingsInteractor {

    override fun getThemeSettings(): ThemeSettings {
        return settingsRepository.getThemeSettings()
    }

    override fun switchTheme(isDark: Boolean) {
        settingsRepository.updateThemeSettings(ThemeSettings(isDark))
    }
}