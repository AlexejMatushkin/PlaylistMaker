package com.practicum.playlistmaker.ui.settings.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.domain.sharing.interactor.SharingInteractor
import com.practicum.playlistmaker.domain.theme.GetThemeSettingsInteractor
import com.practicum.playlistmaker.domain.theme.ThemeManager

class SettingsViewModelFactory(
    private val getThemeSettingsInteractor: GetThemeSettingsInteractor,
    private val themeManager: ThemeManager,
    private val sharingInteractor: SharingInteractor
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingsViewModel(getThemeSettingsInteractor, themeManager, sharingInteractor) as T
    }
}