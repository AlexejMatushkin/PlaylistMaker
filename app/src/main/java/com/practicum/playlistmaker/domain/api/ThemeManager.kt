package com.practicum.playlistmaker.domain.api

interface ThemeManager {
    fun switchTheme(isDark: Boolean)
    fun getCurrentTheme(): Boolean
    fun applyCurrentTheme()
}