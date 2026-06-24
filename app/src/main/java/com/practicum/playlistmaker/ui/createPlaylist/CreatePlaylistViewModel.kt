package com.practicum.playlistmaker.ui.createPlaylist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.playlist.interactor.PlaylistInteractor
import com.practicum.playlistmaker.domain.playlist.model.Playlist
import kotlinx.coroutines.launch

class CreatePlaylistViewModel(
    private val interactor: PlaylistInteractor
) : ViewModel() {

    private val nameStateLiveData = MutableLiveData<NameState>()
    fun observeNameState(): LiveData<NameState> = nameStateLiveData

    fun onNameChanged(name: String) {
        nameStateLiveData.postValue(NameState(isEmpty = name.isEmpty()))
    }

    fun createPlaylist(name: String, description: String, imagePath: String) {
        viewModelScope.launch {
            val playlist = Playlist(
                name = name,
                description = description,
                imagePath = imagePath
            )
            interactor.createPlaylist(playlist)
        }
    }
}
