package com.practicum.playlistmaker.ui.search.fragment

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.domain.search.models.Track
import com.practicum.playlistmaker.ui.player.fragment.PlayerFragment
import com.practicum.playlistmaker.ui.search.adapter.TrackAdapter
import com.practicum.playlistmaker.ui.search.view_model.SearchHistoryState
import com.practicum.playlistmaker.ui.search.view_model.SearchState
import com.practicum.playlistmaker.ui.search.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModel()
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ObsoleteSdkInt")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(
                left = systemBars.left,
                top = systemBars.top,
                right = systemBars.right,
                bottom = systemBars.bottom
            )
            insets
        }

        initViews()
        setupRecyclerView()
        setupSearchField()
        observeViewModel()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            binding.searchEditText.textCursorDrawable = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.cursor_color
            )
        }
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
        rvTracks.layoutManager = LinearLayoutManager(requireContext())

        historyAdapter = TrackAdapter()
        historyAdapter.setOnItemClickListener(itemClickListener)
        searchHistoryRecyclerView.adapter = historyAdapter
        searchHistoryRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun handleTrackClickWithDebounce(track: Track) {
        if (viewModel.clickDebounce()) {
            viewModel.addToHistory(track)

            val bundle = Bundle().apply {
                putParcelable(PlayerFragment.EXTRA_TRACK, track)
            }
            findNavController().navigate(R.id.action_search_to_player, bundle)
        }
    }

    private fun setupSearchField() = binding.apply {
        searchEditText.imeOptions = EditorInfo.IME_ACTION_DONE
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val query = searchEditText.text.toString().trim()
                if (query.isNotEmpty()) {
                    val imm = requireContext().getSystemService(InputMethodManager::class.java)
                    imm?.hideSoftInputFromWindow(searchEditText.windowToken, 0)
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
            val imm = requireContext().getSystemService(InputMethodManager::class.java)
            imm?.hideSoftInputFromWindow(searchEditText.windowToken, 0)
            viewModel.clearSearch()
        }

        searchHistoryClearButton.setOnClickListener {
            viewModel.clearHistory()
        }
    }

    private fun observeViewModel() = binding.apply {
        viewModel.searchState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is SearchState.Loading -> showLoading()
                is SearchState.Success -> showTracks(state.tracks)
                is SearchState.NoResults -> showNoResultsPlaceholder()
                is SearchState.Error -> showErrorPlaceholder()
                is SearchState.Empty -> showEmptyState()
            }
        }

        viewModel.historyState.observe(viewLifecycleOwner) { state ->
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
}
