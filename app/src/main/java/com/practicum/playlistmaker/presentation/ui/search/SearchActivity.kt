package com.practicum.playlistmaker.presentation.ui.search

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.presentation.ui.media.MediaActivity
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.models.Track
import java.util.concurrent.atomic.AtomicBoolean

class SearchActivity : AppCompatActivity() {

    companion object {
        private const val KEY_SEARCH_TEXT = "SEARCH_TEXT"
        private const val KEY_LAST_SEARCH_QUERY = "LAST_SEARCH_QUERY"
        private const val DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    // Интеракторы
    private lateinit var tracksInteractor: TracksInteractor
    private lateinit var searchHistoryInteractor: SearchHistoryInteractor

    // UI элементы
    private lateinit var searchEditText: EditText
    private lateinit var searchClearButton: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: CircularProgressIndicator
    private lateinit var placeholderContainer: View
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderText: TextView
    private lateinit var retryButton: MaterialButton

    private lateinit var searchHistoryContainer: View
    private lateinit var searchHistoryTitle: TextView
    private lateinit var searchHistoryRecyclerView: RecyclerView
    private lateinit var searchHistoryClearButton: MaterialButton

    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private var searchText: String = ""
    private var isSearchInProgress = AtomicBoolean(false)
    private var lastSearchQuery = ""

    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    private var isClickAllowed = true
    private val clickRunnable = Runnable { isClickAllowed = true }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tracksInteractor = Creator.provideTracksInteractor()
        searchHistoryInteractor = Creator.provideSearchHistoryInteractor()

        initViews()
        setupButtons()
        setupRecyclerView()
        setupSearchField()
        setupToolbar()
        showEmptyScreen()
    }

    private fun setupButtons() {
        searchHistoryClearButton.isAllCaps = false

    }

    private fun initViews() {
        searchEditText = findViewById(R.id.search_edit_text)
        searchClearButton = findViewById(R.id.search_clear_button)
        recyclerView = findViewById(R.id.rvTracks)
        progressBar = findViewById(R.id.progressBar)
        placeholderContainer = findViewById(R.id.placeholderContainer)
        placeholderImage = findViewById(R.id.placeholderImage)
        placeholderText = findViewById(R.id.placeholderText)
        retryButton = findViewById(R.id.retryButton)

        searchHistoryContainer = findViewById(R.id.searchHistoryContainer)
        searchHistoryTitle = findViewById(R.id.searchHistoryTitle)
        searchHistoryRecyclerView = findViewById(R.id.searchHistoryRecyclerView)
        searchHistoryClearButton = findViewById(R.id.searchHistoryClearButton)
    }

    private fun setupRecyclerView() {
        val itemClickListener = object : TrackAdapter.OnItemClickListener {
            override fun onItemClick(track: Track) {
                handleTrackClickWithDebounce(track)
            }
        }

        trackAdapter = TrackAdapter()
        trackAdapter.setOnItemClickListener(itemClickListener)
        recyclerView.adapter = trackAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        historyAdapter = TrackAdapter()
        historyAdapter.setOnItemClickListener(itemClickListener)
        searchHistoryRecyclerView.adapter = historyAdapter
        searchHistoryRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun handleTrackClickWithDebounce(track: Track) {
        if (isClickAllowed) {
            isClickAllowed = false

            handleTrackClick(track)

            handler.postDelayed(clickRunnable, CLICK_DEBOUNCE_DELAY)
        }
    }

    private fun handleTrackClick(track: Track) {
        searchHistoryInteractor.addTrack(track)
        updateHistoryVisibility()

        val intent = Intent(this, MediaActivity::class.java).apply {
            putExtra(MediaActivity.EXTRA_TRACK, track)
        }
        startActivity(intent)
    }

    private fun setupSearchField() {
        searchEditText.imeOptions = EditorInfo.IME_ACTION_DONE
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val query = searchEditText.text.toString().trim()
                if (query.isNotEmpty()) {
                    searchRunnable?.let { handler.removeCallbacks(it) }
                    performSearch(query)
                }
                true
            } else {
                false
            }
        }

        searchEditText.doOnTextChanged { text, _, _, _ ->
            searchText = text?.toString() ?: ""
            searchClearButton.isVisible = searchText.isNotEmpty()

            updateHistoryVisibility()

            searchRunnable?.let { handler.removeCallbacks(it) }

            if (searchText.isNotEmpty()) {
                searchRunnable = Runnable {
                    performSearch(searchText)
                }
                handler.postDelayed(searchRunnable!!, DEBOUNCE_DELAY)
            } else {
                showEmptyScreen()
            }
        }

        searchEditText.setOnFocusChangeListener { _, _ ->
            updateHistoryVisibility()
        }

        searchClearButton.setOnClickListener {
            clearSearch()
        }

        searchHistoryClearButton.setOnClickListener {
            searchHistoryInteractor.clearHistory()
            hideHistory()
        }
    }

    private fun setupToolbar() {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar_search)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun showEmptyScreen() {
        recyclerView.isVisible = false
        placeholderContainer.isVisible = false
        progressBar.isVisible = false
        searchHistoryContainer.isVisible = false
    }

    private fun clearSearch() {
        searchEditText.text.clear()
        searchClearButton.isVisible = false

        val imm =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)

        recyclerView.isVisible = false
        placeholderContainer.isVisible = false
        progressBar.isVisible = false
        searchHistoryContainer.isVisible = false

        updateHistoryVisibility()

        searchRunnable?.let { handler.removeCallbacks(it) }
    }

    private fun performSearch(query: String) {

        if (isSearchInProgress.get()) {
            return
        }

        isSearchInProgress.set(true)
        lastSearchQuery = query

        showLoading()

        tracksInteractor.searchTracks(query, object : TracksInteractor.TracksConsumer {
            override fun consume(foundTracks: List<Track>) {
                runOnUiThread {
                    isSearchInProgress.set(false)
                    if (foundTracks.isEmpty()) {

                        showNoResultsPlaceholder()
                    } else {
                        showTracks(foundTracks)
                    }
                }
            }
        })
    }

    private fun showLoading() {
        recyclerView.isVisible = false
        placeholderContainer.isVisible = false
        searchHistoryContainer.isVisible = false
        progressBar.isVisible = true
    }

    private fun showTracks(tracks: List<Track>) {
        progressBar.isVisible = false
        placeholderContainer.isVisible = false
        searchHistoryContainer.isVisible = false
        recyclerView.isVisible = true

        trackAdapter.updateData(tracks)
    }

    private fun showNoResultsPlaceholder() {
        progressBar.isVisible = false
        recyclerView.isVisible = false
        searchHistoryContainer.isVisible = false
        placeholderContainer.isVisible = true

        placeholderImage.setImageResource(R.drawable.ic_placeholder_no_results)
        placeholderText.text = getString(R.string.nothing_found)
        retryButton.isVisible = false
    }

    private fun showErrorPlaceholder() {
        progressBar.isVisible = false
        recyclerView.isVisible = false
        searchHistoryContainer.isVisible = false
        placeholderContainer.isVisible = true

        placeholderImage.setImageResource(R.drawable.ic_placeholder_error_track)
        placeholderText.text = getString(R.string.something_went_wrong)
        placeholderText.maxLines = 4
        retryButton.isVisible = true
        retryButton.isAllCaps = false

        retryButton.setOnClickListener {
            if (lastSearchQuery.isNotEmpty()) {
                performSearch(lastSearchQuery)
            }
        }
    }

    private fun updateHistoryVisibility() {
        val hasFocus = searchEditText.hasFocus()
        val isEmptyText = searchEditText.text.isNullOrEmpty()
        val hasHistory = searchHistoryInteractor.hasHistory()
        if (hasFocus && isEmptyText && hasHistory) {
            showHistory()
        } else {
            hideHistory()
        }
    }

    private fun showHistory() {
        val history = searchHistoryInteractor.getHistory()

        searchHistoryContainer.isVisible = true
        recyclerView.isVisible = false
        placeholderContainer.isVisible = false
        progressBar.isVisible = false

        historyAdapter.updateData(history)

    }

    private fun hideHistory() {
        searchHistoryContainer.isVisible = false
    }

        override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_SEARCH_TEXT, searchText)
        outState.putString(KEY_LAST_SEARCH_QUERY, lastSearchQuery)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        val restoredText = savedInstanceState.getString(KEY_SEARCH_TEXT, "")
        val restoredLastQuery = savedInstanceState.getString(
            KEY_LAST_SEARCH_QUERY,
            ""
        )
        searchText = restoredText
        lastSearchQuery = restoredLastQuery

        searchEditText.setText(restoredText)
        searchEditText.setSelection(restoredText.length)
        searchClearButton.isVisible = restoredText.isNotEmpty()

        updateHistoryVisibility()

        if (restoredLastQuery.isEmpty()) return
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}