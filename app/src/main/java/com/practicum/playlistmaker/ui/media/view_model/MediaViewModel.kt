package com.practicum.playlistmaker.ui.media.view_model

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class MediaViewModel : ViewModel() {

    // Состояние плеера
    private val _playerState = MutableLiveData<MediaState>(MediaState.Default)
    val playerState: LiveData<MediaState> = _playerState

    // Текущая позиция воспроизведения
    private val _currentPosition = MutableLiveData<Long>(0)
    val currentPosition: LiveData<Long> = _currentPosition

    // Медиаплеер
    private var mediaPlayer: MediaPlayer? = null
    private var playbackPosition = 0
    private var progressUpdateJob: Job? = null

    // Данные трека
    private var currentTrack: Track? = null

    fun loadTrack(track: Track) {
        currentTrack = track
        releasePlayer()
        preparePlayer()
    }

    private fun preparePlayer() {
        val previewUrl = currentTrack?.previewUrl
        if (previewUrl.isNullOrBlank()) {
            _playerState.value = MediaState.Error
            return
        }

        _playerState.value = MediaState.Default

        mediaPlayer = MediaPlayer().apply {
            setDataSource(previewUrl)
            prepareAsync()

            setOnPreparedListener {
                _playerState.value = MediaState.Prepared
                if (playbackPosition > 0) {
                    seekTo(playbackPosition)
                }
            }

            setOnCompletionListener {
                _playerState.value = MediaState.Prepared
                _currentPosition.value = 0
                stopProgressUpdates()
            }

            setOnErrorListener { _, _, _ ->
                _playerState.value = MediaState.Error
                true
            }
        }
    }

    fun play() {
        mediaPlayer?.let {
            if (it.isPlaying) return

            if (_playerState.value == MediaState.Prepared || _playerState.value == MediaState.Paused) {
                if (playbackPosition > 0) {
                    it.seekTo(playbackPosition)
                }
                it.start()
                _playerState.value = MediaState.Playing
                startProgressUpdates()
            }
        }
    }

    fun pause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                playbackPosition = it.currentPosition
                it.pause()
                _playerState.value = MediaState.Paused
                stopProgressUpdates()
            }
        }
    }

    fun togglePlay() {
        when (_playerState.value) {
            MediaState.Playing -> pause()
            MediaState.Prepared, MediaState.Paused -> play()
            MediaState.Default -> {
                releasePlayer()
                preparePlayer()
            }
            else -> {}
        }
    }

    fun releasePlayer() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }
        mediaPlayer = null
        playbackPosition = 0
        _playerState.value = MediaState.Default
        _currentPosition.value = 0
        stopProgressUpdates()
    }

    private fun startProgressUpdates() {
        stopProgressUpdates()
        progressUpdateJob = viewModelScope.launch(Dispatchers.Main) {
            while (true) {
                mediaPlayer?.let {
                    if (it.isPlaying) {
                        _currentPosition.value = it.currentPosition.toLong()
                    }
                }
                delay(PROGRESS_UPDATE_INTERVAL_MS)
            }
        }
    }

    private fun stopProgressUpdates() {
        progressUpdateJob?.cancel()
        progressUpdateJob = null
    }

    fun getFormattedTime(millis: Long): String {
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.Companion.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
        stopProgressUpdates()
    }

    companion object {
        private const val PROGRESS_UPDATE_INTERVAL_MS = 300L
    }
}