package com.practicum.playlistmaker.ui.player.view_model

sealed class MediaState {
    object Default : MediaState()
    object Prepared : MediaState()
    object Playing : MediaState()
    object Paused : MediaState()
    object Error : MediaState()
}
