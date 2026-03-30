package com.practicum.playlistmaker.domain.theme.interactor

interface ThemeManager {
    fun switchTheme(isDark: Boolean)
    fun getCurrentTheme(): Boolean
    fun applyCurrentTheme()
}