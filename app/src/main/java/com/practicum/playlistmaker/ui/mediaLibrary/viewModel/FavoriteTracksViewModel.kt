package com.practicum.playlistmaker.ui.mediaLibrary.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.favorite.interactor.FavoriteInteractor
import com.practicum.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(
    private val favoriteInteractor: FavoriteInteractor
) : ViewModel() {

    private val _state = MutableLiveData<FavoriteTracksState>()
    val state: LiveData<FavoriteTracksState> = _state

    init {
        viewModelScope.launch {
            favoriteInteractor.getAllFavorites().collect { tracks ->
                _state.value = if (tracks.isEmpty()) {
                    FavoriteTracksState.Empty
                } else {
                    FavoriteTracksState.Content(tracks)
                }
            }
        }
    }
}

sealed class FavoriteTracksState {
    object Empty : FavoriteTracksState()
    data class Content(val tracks: List<Track>) : FavoriteTracksState()
}
