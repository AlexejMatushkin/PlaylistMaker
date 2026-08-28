package com.practicum.playlistmaker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = Blue,
    onPrimary = White,
    primaryContainer = Blue,
    onPrimaryContainer = White,
    surface = White,
    onSurface = BlackMain,
    onSurfaceVariant = Gray,
    background = White,
    onBackground = BlackMain,
)

private val DarkColors = darkColorScheme(
    primary = BlackMain,
    onPrimary = White,
    primaryContainer = BlackMain,
    onPrimaryContainer = White,
    surface = BlackMain,
    onSurface = White,
    onSurfaceVariant = White,
    background = BlackMain,
    onBackground = White,
)

@Composable
fun PlaylistMakerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        content = content
    )
}