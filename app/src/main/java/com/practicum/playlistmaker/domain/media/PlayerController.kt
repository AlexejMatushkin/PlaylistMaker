package com.practicum.playlistmaker.domain.media

interface PlayerController {
    fun prepare(
        url: String,
        onPrepared: () -> Unit,
        onCompletion: () -> Unit,
        onError: () -> Unit
    )

    fun play()
    fun pause()
    fun seekTo(position: Int)
    fun getCurrentPosition(): Int
    fun isPlaying(): Boolean
    fun release()
    fun showNotification()
    fun hideNotification()
}
