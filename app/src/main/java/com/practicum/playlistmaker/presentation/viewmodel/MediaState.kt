package com.practicum.playlistmaker.presentation.viewmodel

sealed class MediaState {
    object Default : MediaState()
    object Prepared : MediaState()
    object Playing : MediaState()
    object Paused : MediaState()
    object Error : MediaState()
}
