package com.practicum.playlistmaker.ui.playlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.playlist.interactor.PlaylistInteractor
import com.practicum.playlistmaker.domain.playlist.model.Playlist
import com.practicum.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


data class PlaylistState(
    val playlist: Playlist? = null,
    val tracks: List<Track> = emptyList(),
    val totalMinutes: Int = 0
)

class PlaylistViewModel(
    private val interactor: PlaylistInteractor
) : ViewModel() {

    private val stateLiveData = MutableLiveData<PlaylistState>()
    val state: LiveData<PlaylistState> = stateLiveData

    private var playlistJob: Job? = null
    private var currentPlaylistId: Int = 0

    fun loadPlaylist(playlistId: Int) {
        currentPlaylistId = playlistId
        playlistJob?.cancel()
        playlistJob = viewModelScope.launch {
            interactor.getPlaylistById(playlistId).collect { playlist ->
                if (playlist != null) {
                    val trackIds = playlist.trackIds
                    if (trackIds.isNotEmpty()) {
                            interactor.getTracksByIds(trackIds.asReversed()).collect { tracks ->
                            val durationSum = tracks
                                .mapNotNull { it.trackTimeMillis }
                                .sum()
                            val totalMinutes = (durationSum / 1000 / 60).toInt()
                            stateLiveData.value = PlaylistState(
                                playlist = playlist,
                                tracks = tracks,
                                totalMinutes = totalMinutes
                            )
                        }
                    } else {
                        stateLiveData.value = PlaylistState(
                            playlist = playlist,
                            totalMinutes = 0
                        )
                    }
                }
            }
        }
    }

    fun removeTrack(playlistId: Int, trackId: Long) {
        viewModelScope.launch {
            val currentState = stateLiveData.value
            val currentPlaylist = currentState?.playlist ?: return@launch
            interactor.removeTrackFromPlaylist(currentPlaylist, trackId)
            loadPlaylist(playlistId)
        }
    }

    fun getShareText(): String? {
        val state = stateLiveData.value ?: return null
        val playlist = state.playlist ?: return null
        val tracks = state.tracks
        if (tracks.isEmpty()) return null

        val sb = StringBuilder()
        sb.append(playlist.name)
        if (playlist.description.isNotEmpty()) {
            sb.append("\n").append(playlist.description)
        }
        sb.append("\n")
        sb.append(playlist.count).append(" ")
        sb.append(
            when {
                playlist.count % 10 == 1 && playlist.count % 100 != 11 -> "трек"
                playlist.count % 10 in 2..4 && playlist.count % 100 !in 12..14 -> "трека"
                else -> "треков"
            }
        )
        tracks.forEachIndexed { index, track ->
            sb.append("\n")
            sb.append(index + 1).append(". ")
            sb.append(track.artistName).append(" - ").append(track.trackName)
            sb.append(" (").append(track.getFormattedTime()).append(")")
        }
        return sb.toString()
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            val currentState = stateLiveData.value
            val currentPlaylist = currentState?.playlist ?: return@launch
            interactor.deletePlaylist(currentPlaylist)
        }
    }
}
