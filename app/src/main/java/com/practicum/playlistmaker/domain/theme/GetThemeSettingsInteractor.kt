package com.practicum.playlistmaker.domain.theme

import com.practicum.playlistmaker.domain.settings.repository.SettingsRepository

class GetThemeSettingsInteractor(
    private val repository: SettingsRepository
) {
    operator fun invoke(): ThemeSettings = repository.getThemeSettings()
}
