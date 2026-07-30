package com.practicum.playlistmaker.ui.player.view_model

import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.FirebaseAnalytics
import com.practicum.playlistmaker.domain.favorite.interactor.FavoriteInteractor
import com.practicum.playlistmaker.domain.media.PlayerController
import com.practicum.playlistmaker.domain.playlist.interactor.PlaylistInteractor
import com.practicum.playlistmaker.domain.playlist.model.Playlist
import com.practicum.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class PlayerViewModel(
    private val favoriteInteractor: FavoriteInteractor,
    private val playlistInteractor: PlaylistInteractor,
    private val firebaseAnalytics: FirebaseAnalytics
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

    private var playbackPosition = 0
    private var progressUpdateJob: Job? = null
    private var playlistJob: Job? = null
    private var currentTrack: Track? = null
    private var playerController: PlayerController? = null
    private var isAppInBackgrounded = false

    fun onServiceConnected(controller: PlayerController) {
        playerController = controller
        currentTrack?.let { track ->
            track.previewUrl?.let { url ->
                controller.prepare(
                    url = url,
                    onPrepared = {
                        playerState = PlayerState.Prepared
                        if (playbackPosition > 0) {
                            controller.seekTo(playbackPosition)
                        }
                        updateScreenState()
                    },
                    onCompletion = {
                        playerState = PlayerState.Prepared
                        currentPosition = 0
                        stopProgressUpdates()
                        if (isAppInBackgrounded) {
                            playerController?.hideNotification()
                        }
                        updateScreenState()
                    },
                    onError = {
                        playerState = PlayerState.Error
                        updateScreenState()
                    }
                )
            }
        }
    }

    fun loadTrack(track: Track) {
        currentTrack = track
        viewModelScope.launch {
            isFavorite = favoriteInteractor.isFavorite(track.trackId)
            updateScreenState()
        }
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
                val bundle = bundleOf("track_name" to track.trackName)
                firebaseAnalytics.logEvent("track_added_to_favorites", bundle)
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

    fun onAppBackgrounded() {
        isAppInBackgrounded = true
        if (playerState == PlayerState.Playing) {
            playerController?.showNotification()
        }
    }

    fun onAppForegrounded() {
        isAppInBackgrounded = false
        playerController?.hideNotification()
    }

    fun play() {
        val controller = playerController ?: return
        if (controller.isPlaying()) return

        if (playerState == PlayerState.Prepared || playerState == PlayerState.Paused) {
            if (playbackPosition > 0) {
                controller.seekTo(playbackPosition)
            }
            controller.play()
            playerState = PlayerState.Playing
            updateScreenState()
            startProgressUpdates()
        }
    }

    fun pause() {
        val controller = playerController ?: return
        if (controller.isPlaying()) {
            playbackPosition = controller.getCurrentPosition()
            controller.pause()
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
        playerController?.let { controller ->
            controller.hideNotification()
            controller.release()
        }
        playbackPosition = 0
        playerState = PlayerState.Default
        currentPosition = 0
        isAppInBackgrounded = false
        updateScreenState()
        stopProgressUpdates()
    }

    private fun updateScreenState() {
        _screenState.value = PlayerScreenState(
            playerState = playerState,
            currentPosition = currentPosition,
            isFavorite = isFavorite
        )
    }

    private fun preparePlayer() {
        val track = currentTrack
        val previewUrl = track?.previewUrl
        if (previewUrl.isNullOrBlank()) {
            playerState = PlayerState.Error
            updateScreenState()
            return
        }

        playerState = PlayerState.Default
        updateScreenState()

        playerController?.let { controller ->
            controller.prepare(
                url = previewUrl,
                onPrepared = {
                    playerState = PlayerState.Prepared
                    if (playbackPosition > 0) {
                        controller.seekTo(playbackPosition)
                    }
                    updateScreenState()
                },
                onCompletion = {
                    playerState = PlayerState.Prepared
                    currentPosition = 0
                    stopProgressUpdates()
                    if (isAppInBackgrounded) {
                        playerController?.hideNotification()
                    }
                    updateScreenState()
                },
                onError = {
                    playerState = PlayerState.Error
                    updateScreenState()
                }
            )
        }
    }

    private fun startProgressUpdates() {
        stopProgressUpdates()
        progressUpdateJob = viewModelScope.launch {
            while (true) {
                if (playerController?.isPlaying() == true) {
                    currentPosition = playerController?.getCurrentPosition()?.toLong() ?: 0
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
