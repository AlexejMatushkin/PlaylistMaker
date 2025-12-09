package com.practicum.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.view.View
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import java.util.concurrent.atomic.AtomicBoolean

class SearchActivity : AppCompatActivity() {

    private lateinit var searchEditText: EditText
    private lateinit var searchClearButton: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var placeholderContainer: View
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderText: TextView
    private lateinit var retryButton: MaterialButton

    private lateinit var trackAdapter: TrackAdapter
    private val repository = SearchRepository(iTunesApiService.create())

    private var searchText: String = ""
    private var isSearchInProgress = AtomicBoolean(false)
    private var lastSearchQuery = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        setupRecyclerView()
        setupSearchField()
        setupToolbar()
        showEmptyScreen()
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
    }

    private fun setupRecyclerView() {
        trackAdapter = TrackAdapter()
        recyclerView.adapter = trackAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupSearchField() {
        searchEditText.imeOptions = EditorInfo.IME_ACTION_DONE
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val query = searchEditText.text.toString().trim()
                if (query.isNotEmpty()) {
                    performSearch(query)
                }
                true
            } else {
                false
            }
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchText = s?.toString() ?: ""
                searchClearButton.isVisible = searchText.isNotEmpty()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        searchClearButton.setOnClickListener {
            clearSearch()
        }
    }

    private fun setupToolbar() {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar_search)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun showEmptyScreen() {
        recyclerView.visibility = View.GONE
        placeholderContainer.visibility = View.GONE
        progressBar.visibility = View.GONE
    }

    private fun clearSearch() {
        searchEditText.text.clear()
        searchClearButton.visibility = View.GONE

        val imm =
            getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)

        recyclerView.visibility = View.GONE

        placeholderContainer.visibility = View.GONE
    }

    private fun performSearch(query: String) {

        if (isSearchInProgress.get()) {
            return
        }

        isSearchInProgress.set(true)
        lastSearchQuery = query

        showLoading()

        repository.searchTracks(query, object : SearchRepository.SearchCallback {
            override fun onSuccess(tracks: List<Track>) {
                runOnUiThread {
                    isSearchInProgress.set(false)
                    if (tracks.isEmpty()) {

                        showNoResultsPlaceholder()
                    } else {
                        showTracks(tracks)
                    }
                }
            }

            override fun onError(error: String) {
                runOnUiThread {
                    isSearchInProgress.set(false)

                    showErrorPlaceholder()
                }
            }
        })
    }

    private fun showLoading() {
        recyclerView.visibility = View.GONE
        placeholderContainer.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }

    private fun showTracks(tracks: List<Track>) {
        progressBar.visibility = View.GONE
        placeholderContainer.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE

        trackAdapter.updateData(tracks)
    }

    private fun showNoResultsPlaceholder() {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
        placeholderContainer.visibility = View.VISIBLE

        placeholderImage.setImageResource(R.drawable.ic_placeholder_no_results)
        placeholderText.text = getString(R.string.nothing_found)
        retryButton.visibility = View.GONE
    }

    private fun showErrorPlaceholder() {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
        placeholderContainer.visibility = View.VISIBLE

        placeholderImage.setImageResource(R.drawable.ic_placeholder_error_track)
        placeholderText.text = getString(R.string.something_went_wrong)
        retryButton.visibility = View.VISIBLE

        retryButton.setOnClickListener {
            if (lastSearchQuery.isNotEmpty()) {
                performSearch(lastSearchQuery)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("SEARCH_TEXT", searchText)
        outState.putString("LAST_SEARCH_QUERY", lastSearchQuery)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        val restoredText = savedInstanceState.getString("SEARCH_TEXT", "")
        val restoredLastQuery = savedInstanceState.getString(
            "LAST_SEARCH_QUERY",
            ""
        )
        searchText = restoredText
        lastSearchQuery = restoredLastQuery

        searchEditText.setText(restoredText)
        searchEditText.setSelection(restoredText.length)
        searchClearButton.isVisible = restoredText.isNotEmpty()
        if (restoredLastQuery.isEmpty()) return
    }
}
