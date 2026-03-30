package com.practicum.playlistmaker.domain.theme

interface ThemeManager {
    fun switchTheme(isDark: Boolean)
    fun getCurrentTheme(): Boolean
    fun applyCurrentTheme()
}