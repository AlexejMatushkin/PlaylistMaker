package com.practicum.playlistmaker.ui.search.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.search.interactor.SearchHistoryInteractor
import com.practicum.playlistmaker.domain.search.interactor.TracksInteractor
import com.practicum.playlistmaker.domain.search.models.SearchResult
import com.practicum.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    private var searchJob: Job? = null

    private val _searchState = MutableLiveData<SearchState>(SearchState.Empty)
    val searchState: LiveData<SearchState> = _searchState

    private val _historyState = MutableLiveData<SearchHistoryState>()
    val historyState: LiveData<SearchHistoryState> = _historyState

    private var lastQuery = ""
    private var clickJob: Job? = null

    init {
        loadHistory()
    }

    fun loadHistory() {
        viewModelScope.launch {
            val history = searchHistoryInteractor.getHistory()
            _historyState.value = if (history.isEmpty()) {
                SearchHistoryState.Empty
            } else {
                SearchHistoryState.History(history)
            }
        }
    }

    fun searchWithDebounce(query: String) {
        if (query.isBlank()) {
            _searchState.value = SearchState.Empty
            return
        }

        lastQuery = query

        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            performSearch(query)
        }
    }

    fun searchImmediately(query: String) {
        if (query.isBlank()) {
            _searchState.value = SearchState.Empty
            return
        }

        lastQuery = query

        searchJob?.cancel()

        performSearch(query)
    }

    private fun performSearch(query: String) {
        _searchState.value = SearchState.Loading
        viewModelScope.launch {
            tracksInteractor.searchTracks(query).collect { result ->
                _searchState.value = when (result) {
                    is SearchResult.Success -> {
                        if (result.tracks.isEmpty()) SearchState.NoResults
                        else SearchState.Success(result.tracks)
                    }
                    is SearchResult.Error -> SearchState.Error
                }
            }
        }
    }

    fun addToHistory(track: Track) {
        viewModelScope.launch {
            searchHistoryInteractor.addTrack(track)
            loadHistory()
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            searchHistoryInteractor.clearHistory()
            _historyState.value = SearchHistoryState.Empty
        }
    }

    fun clearSearch() {
        _searchState.value = SearchState.Empty
        lastQuery = ""
    }

    fun retryLastSearch() {
        if (lastQuery.isNotBlank()) {
            performSearch(lastQuery)
        }
    }

    fun onFocusChanged(hasFocus: Boolean) {
        if (hasFocus && _searchState.value is SearchState.Empty) {
            loadHistory()
        } else {
            if (!hasFocus && _searchState.value is SearchState.Empty) {
                _historyState.value = SearchHistoryState.Empty
            }
        }
    }

    fun clickDebounce(): Boolean {
       return if (clickJob?.isActive == true) {
           false
       } else {
           clickJob = viewModelScope.launch {
               delay(CLICK_DEBOUNCE_DELAY)
           }
           true
       }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}
