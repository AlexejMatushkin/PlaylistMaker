package com.practicum.playlistmaker.ui.player.view_model

sealed class PlayerState {
    object Default : PlayerState()
    object Prepared : PlayerState()
    object Playing : PlayerState()
    object Paused : PlayerState()
    object Error : PlayerState()
}

data class PlayerScreenState(
    val playerState: PlayerState = PlayerState.Default,
    val currentPosition: Long = 0,
    val isFavorite: Boolean = false
)