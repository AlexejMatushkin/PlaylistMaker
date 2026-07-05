package com.practicum.playlistmaker.ui.createPlaylist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.playlist.interactor.PlaylistInteractor
import com.practicum.playlistmaker.domain.playlist.model.Playlist
import com.practicum.playlistmaker.domain.playlist.repository.PlaylistImageRepository
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    private val editInteractor: PlaylistInteractor,
    imageRepository: PlaylistImageRepository
) : CreatePlaylistViewModel(editInteractor, imageRepository) {

    private var currentPlaylist: Playlist? = null

    private val _loadedPlaylist = MutableLiveData<Playlist?>()
    fun observeLoadedPlaylist(): LiveData<Playlist?> = _loadedPlaylist

    fun loadPlaylist(playlistId: Int) {
        viewModelScope.launch {
            editInteractor.getPlaylistById(playlistId).collect { playlist ->
                if (playlist != null) {
                    currentPlaylist = playlist
                    onNameChanged(playlist.name)
                    _loadedPlaylist.postValue(playlist)
                }
            }
        }
    }

    override fun createPlaylist(name: String, description: String) {
        viewModelScope.launch {
            val existing = currentPlaylist ?: return@launch
            val imagePath = savedImagePath.value ?: existing.imagePath
            val updated = existing.copy(
                name = name,
                description = description,
                imagePath = imagePath
            )
            editInteractor.updatePlaylist(updated)
        }
    }
}
