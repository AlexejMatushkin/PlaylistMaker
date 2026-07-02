package com.practicum.playlistmaker.ui.createPlaylist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.playlist.interactor.PlaylistInteractor
import com.practicum.playlistmaker.domain.playlist.model.Playlist
import com.practicum.playlistmaker.domain.playlist.repository.PlaylistImageRepository
import kotlinx.coroutines.launch

open class CreatePlaylistViewModel(
    protected val interactor: PlaylistInteractor,
    private val imageRepository: PlaylistImageRepository
) : ViewModel() {

    private val nameStateLiveData = MutableLiveData<NameState>()
    fun observeNameState(): LiveData<NameState> = nameStateLiveData

    protected val savedImagePath = MutableLiveData<String?>()
    fun observeSavedImagePath(): LiveData<String?> = savedImagePath

    fun onNameChanged(name: String) {
        nameStateLiveData.postValue(NameState(isEmpty = name.isEmpty()))
    }

    fun saveImage(uriString: String) {
        savedImagePath.postValue(imageRepository.saveImageToPrivateStorage(uriString))
    }

    open fun createPlaylist(name: String, description: String) {
        viewModelScope.launch {
            val playlist = Playlist(
                name = name,
                description = description,
                imagePath = savedImagePath.value ?: ""
            )
            interactor.createPlaylist(playlist)
        }
    }
}
