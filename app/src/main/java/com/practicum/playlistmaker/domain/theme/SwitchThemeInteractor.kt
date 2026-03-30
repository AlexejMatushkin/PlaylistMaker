package com.practicum.playlistmaker.domain.theme

import com.practicum.playlistmaker.domain.settings.repository.SettingsRepository

class SwitchThemeInteractor(
    private val repository: SettingsRepository
) {
    operator fun invoke(isDark: Boolean) {
        repository.updateThemeSettings(ThemeSettings(isDark))
    }
}
