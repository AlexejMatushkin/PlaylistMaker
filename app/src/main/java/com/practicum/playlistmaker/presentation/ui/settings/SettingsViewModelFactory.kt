package com.practicum.playlistmaker.presentation.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.domain.api.SettingsInteractor
import com.practicum.playlistmaker.domain.api.ThemeManager
import com.practicum.playlistmaker.presentation.viewmodel.SettingsViewModel

class SettingsViewModelFactory(
    private val settingsInteractor: SettingsInteractor,
    private val themeManager: ThemeManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return SettingsViewModel(settingsInteractor, themeManager) as T
    }
}
