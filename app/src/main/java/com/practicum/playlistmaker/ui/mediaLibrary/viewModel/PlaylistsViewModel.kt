package com.practicum.playlistmaker.ui.mediaLibrary.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.playlist.interactor.PlaylistInteractor
import com.practicum.playlistmaker.domain.playlist.model.Playlist
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val interactor: PlaylistInteractor
) : ViewModel() {

    private val stateLiveData = MutableLiveData<PlaylistState>(PlaylistState.Empty)
    val state: LiveData<PlaylistState> = stateLiveData

    init {
        loadPlaylists()
    }

    fun loadPlaylists() {
        viewModelScope.launch {
            interactor.getAllPlaylists().collect { playlists ->
                processResult(playlists)
            }
        }
    }

    private fun processResult(playlists: List<Playlist>) {
        if (playlists.isEmpty()) {
            stateLiveData.postValue(PlaylistState.Empty)
        } else {
            stateLiveData.postValue(PlaylistState.Content(playlists))
        }
    }
}
