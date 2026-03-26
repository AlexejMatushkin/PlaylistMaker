package com.practicum.playlistmaker.presentation.ui.search

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.presentation.ui.media.MediaActivity
import com.practicum.playlistmaker.presentation.viewmodel.SearchHistoryState
import com.practicum.playlistmaker.presentation.viewmodel.SearchState
import com.practicum.playlistmaker.presentation.viewmodel.SearchViewModel

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var viewModel: SearchViewModel
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true
    private val clickRunnable = Runnable { isClickAllowed = true }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel = ViewModelProvider(
            this,
            SearchViewModelFactory(
                Creator.provideTracksInteractor(),
                Creator.provideSearchHistoryInteractor()
            )
        )[SearchViewModel::class.java]

        initViews()
        setupRecyclerView()
        setupSearchField()
        setupToolbar()
        observeViewModel()
    }

    private fun initViews() {
        binding.searchHistoryClearButton.isAllCaps = false
    }

    private fun setupRecyclerView() = binding.apply {
        val itemClickListener = object : TrackAdapter.OnItemClickListener {
            override fun onItemClick(track: Track) {
                handleTrackClickWithDebounce(track)
            }
        }

        trackAdapter = TrackAdapter()
        trackAdapter.setOnItemClickListener(itemClickListener)
        rvTracks.adapter = trackAdapter
        rvTracks.layoutManager = LinearLayoutManager(this@SearchActivity)

        historyAdapter = TrackAdapter()
        historyAdapter.setOnItemClickListener(itemClickListener)
        searchHistoryRecyclerView.adapter = historyAdapter
        searchHistoryRecyclerView.layoutManager = LinearLayoutManager(this@SearchActivity)
    }

    private fun handleTrackClickWithDebounce(track: Track) {
        if (isClickAllowed) {
            isClickAllowed = false
            viewModel.addToHistory(track)
            handler.postDelayed(clickRunnable, CLICK_DEBOUNCE_DELAY)

            val intent = Intent(this, MediaActivity::class.java).apply {
                putExtra(MediaActivity.EXTRA_TRACK, track)
            }
            startActivity(intent)
        }
    }

    private fun setupSearchField() = binding.apply {
        searchEditText.imeOptions = EditorInfo.IME_ACTION_DONE
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val query = searchEditText.text.toString().trim()
                if (query.isNotEmpty()) {
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
                    searchEditText.clearFocus()
                    viewModel.searchImmediately(query)
                }
                true
            } else {
                false
            }
        }

        searchEditText.doOnTextChanged { text, _, _, _ ->
            val searchText = text?.toString() ?: ""
            searchClearButton.isVisible = searchText.isNotEmpty()

            if (searchText.isNotEmpty()) {
                viewModel.searchWithDebounce(searchText)
            } else {
                viewModel.clearSearch()
                viewModel.loadHistory()
            }
        }

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            viewModel.onFocusChanged(hasFocus)
        }

        searchClearButton.setOnClickListener {
            searchEditText.text.clear()
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
            viewModel.clearSearch()
        }

        searchHistoryClearButton.setOnClickListener {
            viewModel.clearHistory()
        }
    }

    private fun setupToolbar() {
        binding.toolbarSearch.setNavigationOnClickListener {
            finish()
        }
    }

    private fun observeViewModel() = binding.apply {
        viewModel.searchState.observe(this@SearchActivity) { state ->
            when (state) {
                is SearchState.Loading -> showLoading()
                is SearchState.Success -> showTracks(state.tracks)
                is SearchState.NoResults -> showNoResultsPlaceholder()
                is SearchState.Error -> showErrorPlaceholder()
                is SearchState.Empty -> showEmptyState()
            }
        }

        viewModel.historyState.observe(this@SearchActivity) { state ->
            when (state) {
                is SearchHistoryState.History -> showHistory(state.tracks)
                is SearchHistoryState.Empty -> hideHistory()
            }
        }
    }

    private fun showLoading() = binding.apply {
        progressBar.isVisible = true
        rvTracks.isVisible = false
        placeholderContainer.isVisible = false
        searchHistoryContainer.isVisible = false
    }

    private fun showTracks(tracks: List<Track>) = binding.apply {
        progressBar.isVisible = false
        rvTracks.isVisible = true
        placeholderContainer.isVisible = false
        searchHistoryContainer.isVisible = false
        trackAdapter.updateData(tracks)
    }

    private fun showNoResultsPlaceholder() = binding.apply {
        progressBar.isVisible = false
        rvTracks.isVisible = false
        searchHistoryContainer.isVisible = false
        placeholderContainer.isVisible = true
        placeholderImage.setImageResource(R.drawable.ic_placeholder_no_results)
        placeholderText.text = getString(R.string.nothing_found)
        retryButton.isVisible = false
    }

    private fun showErrorPlaceholder() = binding.apply {
        progressBar.isVisible = false
        rvTracks.isVisible = false
        searchHistoryContainer.isVisible = false
        placeholderContainer.isVisible = true
        placeholderImage.setImageResource(R.drawable.ic_placeholder_error_track)
        placeholderText.text = getString(R.string.something_went_wrong)
        placeholderText.maxLines = 4
        retryButton.isVisible = true
        retryButton.isAllCaps = false
        retryButton.setOnClickListener {
            viewModel.retryLastSearch()
        }
    }

    private fun showEmptyState() = binding.apply {
        progressBar.isVisible = false
        rvTracks.isVisible = false
        placeholderContainer.isVisible = false
        if (searchEditText.hasFocus()) {
            viewModel.loadHistory()
        } else {
            searchHistoryContainer.isVisible = false
        }
    }

    private fun showHistory(tracks: List<Track>) = binding.apply {
        if (tracks.isNotEmpty() &&
            searchEditText.hasFocus() &&
            searchEditText.text.isNullOrEmpty() &&
            viewModel.searchState.value is SearchState.Empty) {
            searchHistoryContainer.isVisible = true
            rvTracks.isVisible = false
            placeholderContainer.isVisible = false
            progressBar.isVisible = false
            historyAdapter.updateData(tracks)
        } else {
            searchHistoryContainer.isVisible = false
        }
    }

    private fun hideHistory() = binding.apply {
        searchHistoryContainer.isVisible = false
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}
