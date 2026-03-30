package com.practicum.playlistmaker.domain.theme.interactor

import com.practicum.playlistmaker.domain.settings.repository.SettingsRepository
import com.practicum.playlistmaker.domain.theme.ThemeSettings

class SwitchThemeInteractor(
    private val repository: SettingsRepository
) {
    operator fun invoke(isDark: Boolean) {
        repository.updateThemeSettings(ThemeSettings(isDark))
    }
}