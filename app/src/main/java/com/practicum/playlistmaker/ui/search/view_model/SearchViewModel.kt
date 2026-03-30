package com.practicum.playlistmaker.ui.search.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.search.interactor.SearchHistoryInteractor
import com.practicum.playlistmaker.domain.search.models.SearchResult
import com.practicum.playlistmaker.domain.search.models.Track
import com.practicum.playlistmaker.domain.search.interactor.TracksInteractor
import kotlinx.coroutines.launch

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    private val _searchState = MutableLiveData<SearchState>(SearchState.Empty)
    val searchState: LiveData<SearchState> = _searchState

    private val _historyState = MutableLiveData<SearchHistoryState>()
    val historyState: LiveData<SearchHistoryState> = _historyState

    private var lastQuery = ""

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

        searchRunnable?.let { handler.removeCallbacks(it) }

        searchRunnable = Runnable {
            performSearch(query)
        }
        handler.postDelayed(searchRunnable!!, SEARCH_DEBOUNCE_DELAY)
    }

    fun searchImmediately(query: String) {
        if (query.isBlank()) {
            _searchState.value = SearchState.Empty
            return
        }

        lastQuery = query

        searchRunnable?.let { handler.removeCallbacks(it) }

        performSearch(query)
    }

    private fun performSearch(query: String) {
        _searchState.value = SearchState.Loading

        tracksInteractor.searchTracks(query, object : TracksInteractor.TracksConsumer {
            override fun consume(result: SearchResult) {
                viewModelScope.launch {
                    _searchState.value = when (result) {
                        is SearchResult.Success -> {
                            if (result.tracks.isEmpty()) {
                                SearchState.NoResults
                            } else {
                                SearchState.Success(result.tracks)
                            }
                        }
                        is SearchResult.Error -> {
                            SearchState.Error
                        }
                    }
                }
            }
        })
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

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(null)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}