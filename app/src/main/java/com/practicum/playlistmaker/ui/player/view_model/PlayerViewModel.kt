package com.practicum.playlistmaker.ui.player.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.favorite.interactor.FavoriteInteractor
import com.practicum.playlistmaker.domain.media.MediaPlayerRepository
import com.practicum.playlistmaker.domain.playlist.interactor.PlaylistInteractor
import com.practicum.playlistmaker.domain.playlist.model.Playlist
import com.practicum.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class PlayerViewModel(
    private val mediaPlayerRepository: MediaPlayerRepository,
    private val favoriteInteractor: FavoriteInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _screenState = MutableLiveData(PlayerScreenState())
    val screenState: LiveData<PlayerScreenState> = _screenState

    private val _playlistState = MutableLiveData<PlaylistSheetState>(PlaylistSheetState.Hidden)
    val playlistState: LiveData<PlaylistSheetState> = _playlistState

    private val _addTrackResult = MutableLiveData<AddTrackResult?>()
    val addTrackResult: LiveData<AddTrackResult?> = _addTrackResult

    private var playerState: PlayerState = PlayerState.Default
    private var currentPosition: Long = 0
    private var isFavorite: Boolean = false

    private fun updateScreenState() {
        _screenState.value = PlayerScreenState(
            playerState = playerState,
            currentPosition = currentPosition,
            isFavorite = isFavorite
        )
    }

    private var playbackPosition = 0
    private var progressUpdateJob: Job? = null
    private var playlistJob: Job? = null
    private var currentTrack: Track? = null

    fun loadTrack(track: Track) {
        currentTrack = track
        viewModelScope.launch {
            isFavorite = favoriteInteractor.isFavorite(track.trackId)
            updateScreenState()
        }
        releasePlayer()
        preparePlayer()
    }

    fun onFavoriteClicked() {
        val track = currentTrack ?: return
        viewModelScope.launch {
            if (isFavorite) {
                favoriteInteractor.removeFromFavorites(track)
                isFavorite = false
            } else {
                favoriteInteractor.addToFavorites(track)
                isFavorite = true
            }
            updateScreenState()
        }
    }

    fun loadPlaylistsForSheet() {
        playlistJob?.cancel()
        playlistJob = viewModelScope.launch {
            playlistInteractor.getAllPlaylists().collect { playlists ->
                if (playlists.isEmpty()) {
                    _playlistState.value = PlaylistSheetState.Empty
                } else {
                    _playlistState.value = PlaylistSheetState.Content(playlists)
                }
            }
        }
    }

    fun showSheet() {
        _playlistState.value = PlaylistSheetState.Loading
        loadPlaylistsForSheet()
    }

    fun hideSheet() {
        playlistJob?.cancel()
        _playlistState.value = PlaylistSheetState.Hidden
    }

    fun addTrackToPlaylist(playlist: Playlist) {
        val track = currentTrack ?: return
        playlistJob?.cancel()
        viewModelScope.launch {
            if (playlist.trackIds.contains(track.trackId)) {
                _addTrackResult.value = AddTrackResult.AlreadyInPlaylist(playlist.name)
            } else {
                playlistInteractor.addTrackToPlaylist(playlist, track)
                _addTrackResult.value = AddTrackResult.Added(playlist.name)
            }
        }
    }

    fun clearAddTrackResult() {
        _addTrackResult.value = null
    }

    private fun preparePlayer() {
        val previewUrl = currentTrack?.previewUrl
        if (previewUrl.isNullOrBlank()) {
            playerState = PlayerState.Error
            updateScreenState()
            return
        }

        playerState = PlayerState.Default
        updateScreenState()

        mediaPlayerRepository.preparePlayer(
            url = previewUrl,
            onPrepared = {
                playerState = PlayerState.Prepared
                if (playbackPosition > 0) {
                    mediaPlayerRepository.seekTo(playbackPosition)
                }
                updateScreenState()
            },
            onCompletion = {
                playerState = PlayerState.Prepared
                currentPosition = 0
                stopProgressUpdates()
                updateScreenState()
            },
            onError = {
                playerState = PlayerState.Error
                updateScreenState()
            }
        )
    }

    fun play() {
        if (mediaPlayerRepository.isPlaying()) return

        if (playerState == PlayerState.Prepared || playerState == PlayerState.Paused) {
            if (playbackPosition > 0) {
                mediaPlayerRepository.seekTo(playbackPosition)
            }
            mediaPlayerRepository.play()
            playerState = PlayerState.Playing
            updateScreenState()
            startProgressUpdates()
        }
    }

    fun pause() {
        if (mediaPlayerRepository.isPlaying()) {
            playbackPosition = mediaPlayerRepository.getCurrentPosition()
            mediaPlayerRepository.pause()
            playerState = PlayerState.Paused
            updateScreenState()
            stopProgressUpdates()
        }
    }

    fun togglePlay() {
        when (playerState) {
            PlayerState.Playing -> pause()
            PlayerState.Prepared, PlayerState.Paused -> play()
            else -> {}
        }
    }

    fun releasePlayer() {
        mediaPlayerRepository.release()
        playbackPosition = 0
        playerState = PlayerState.Default
        currentPosition = 0
        updateScreenState()
        stopProgressUpdates()
    }

    private fun startProgressUpdates() {
        stopProgressUpdates()
        progressUpdateJob = viewModelScope.launch {
            while (true) {
                if (mediaPlayerRepository.isPlaying()) {
                    currentPosition = mediaPlayerRepository.getCurrentPosition().toLong()
                    updateScreenState()
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
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
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

sealed interface PlaylistSheetState {
    data object Hidden : PlaylistSheetState
    data object Loading : PlaylistSheetState
    data object Empty : PlaylistSheetState
    data class Content(val playlists: List<Playlist>) : PlaylistSheetState
}

sealed interface AddTrackResult {
    data class Added(val playlistName: String) : AddTrackResult
    data class AlreadyInPlaylist(val playlistName: String) : AddTrackResult
}
