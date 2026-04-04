package com.practicum.playlistmaker.domain.media

interface MediaPlayerRepository {
    fun preparePlayer(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit, onError: () -> Unit)
    fun play()
    fun pause()
    fun getCurrentPosition(): Int
    fun release()
    fun isPlaying(): Boolean
    fun seekTo(position: Int)
}
