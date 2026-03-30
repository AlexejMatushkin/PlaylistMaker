package com.practicum.playlistmaker.domain.theme.interactor

import com.practicum.playlistmaker.domain.settings.repository.SettingsRepository
import com.practicum.playlistmaker.domain.theme.ThemeSettings

class GetThemeSettingsInteractor(
    private val repository: SettingsRepository
) {
    operator fun invoke(): ThemeSettings = repository.getThemeSettings()
}