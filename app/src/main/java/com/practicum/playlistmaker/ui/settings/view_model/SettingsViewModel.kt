package com.practicum.playlistmaker.ui.settings.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.sharing.interactor.SharingInteractor
import com.practicum.playlistmaker.domain.theme.interactor.GetThemeSettingsInteractor
import com.practicum.playlistmaker.domain.theme.interactor.ThemeManager
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val getThemeSettingsInteractor: GetThemeSettingsInteractor,
    private val themeManager: ThemeManager,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

    private val _themeState = MutableLiveData<Boolean>()
    val themeState: LiveData<Boolean> = _themeState

    init {
        loadTheme()
    }

    private fun loadTheme() {
        val themeSettings = getThemeSettingsInteractor()
        _themeState.value = themeSettings.isDarkTheme
    }

    fun switchTheme(isDark: Boolean) {
        viewModelScope.launch {
            themeManager.switchTheme(isDark)
            _themeState.value = isDark
        }
    }

    fun shareApp() {
        sharingInteractor.shareApp()
    }

    fun sendSupport() {
        sharingInteractor.openSupport()
    }

    fun openAgreement() {
        sharingInteractor.openTerms()
    }
}
