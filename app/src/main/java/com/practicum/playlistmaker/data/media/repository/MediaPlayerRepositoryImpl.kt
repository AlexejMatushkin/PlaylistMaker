package com.practicum.playlistmaker.data.media.repository

import android.media.MediaPlayer
import com.practicum.playlistmaker.domain.media.MediaPlayerRepository

class MediaPlayerRepositoryImpl(
    private val mediaPlayerFactory: () -> MediaPlayer
) : MediaPlayerRepository {

    private var mediaPlayer: MediaPlayer? = null
    private var onPreparedCallback: (() -> Unit)? = null
    private var onCompletionCallback: (() -> Unit)? = null
    private var onErrorCallback: (() -> Unit)? = null
    private var isPrepared = false

    override fun preparePlayer(
        url: String,
        onPrepared: () -> Unit,
        onCompletion: () -> Unit,
        onError: () -> Unit
    ) {
        release()

        this.onPreparedCallback = onPrepared
        this.onCompletionCallback = onCompletion
        this.onErrorCallback = onError
        isPrepared = false

        mediaPlayer = mediaPlayerFactory().apply {
            setDataSource(url)
            prepareAsync()

            setOnPreparedListener {
                isPrepared = true
                this@MediaPlayerRepositoryImpl.onPreparedCallback?.invoke()
            }

            setOnCompletionListener {
                isPrepared = false
                this@MediaPlayerRepositoryImpl.onCompletionCallback?.invoke()
            }

            setOnErrorListener { _, _, _ ->
                isPrepared = false
                this@MediaPlayerRepositoryImpl.onErrorCallback?.invoke()
                true
            }
        }
    }

    override fun play() {
        mediaPlayer?.start()
    }

    override fun pause() {
        mediaPlayer?.pause()
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }

    override fun release() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }
        mediaPlayer = null
        onPreparedCallback = null
        onCompletionCallback = null
        onErrorCallback = null
        isPrepared = false
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }

    override fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
    }
}
