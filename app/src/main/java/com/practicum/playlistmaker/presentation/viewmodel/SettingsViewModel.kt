package com.practicum.playlistmaker.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.api.SettingsInteractor
import com.practicum.playlistmaker.domain.api.ThemeManager
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val themeManager: ThemeManager
) : ViewModel() {

    private val _themeState = MutableLiveData<Boolean>()
    val themeState: LiveData<Boolean> = _themeState
    private val _shareEvent = MutableLiveData<Unit>()
    val shareEvent: LiveData<Unit> = _shareEvent

    private val _supportEvent = MutableLiveData<Unit>()
    val supportEvent: LiveData<Unit> = _supportEvent

    private val _agreementEvent = MutableLiveData<Unit>()
    val agreementEvent: LiveData<Unit> = _agreementEvent

    init {
        loadTheme()
    }

    private fun loadTheme() {
        val themeSettings = settingsInteractor.getThemeSettings()
        _themeState.value = themeSettings.isDarkTheme
    }

    fun switchTheme(isDark: Boolean) {
        viewModelScope.launch {
            themeManager.switchTheme(isDark)
            _themeState.value = isDark
        }
    }

    fun shareApp() {
        _shareEvent.value = Unit
    }

    fun sendSupport() {
        _supportEvent.value = Unit
    }

    fun openAgreement() {
        _agreementEvent.value = Unit
    }
}