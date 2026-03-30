package com.practicum.playlistmaker.ui.settings.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.domain.sharing.interactor.SharingInteractor
import com.practicum.playlistmaker.domain.theme.interactor.GetThemeSettingsInteractor
import com.practicum.playlistmaker.domain.theme.interactor.ThemeManager

class SettingsViewModelFactory(
    private val getThemeSettingsInteractor: GetThemeSettingsInteractor,
    private val themeManager: ThemeManager,
    private val sharingInteractor: SharingInteractor
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return SettingsViewModel(getThemeSettingsInteractor, themeManager, sharingInteractor) as T
    }
}
