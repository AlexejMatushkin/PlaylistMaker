package com.practicum.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton

class SearchActivity : AppCompatActivity() {

    private var searchText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val searchEditText = findViewById<EditText>(R.id.search_edit_text)
        val searchBackButton = findViewById<MaterialToolbar>(R.id.toolbar_search)
        val searchClearButton = findViewById<ImageButton>(R.id.search_clear_button)

        searchEditText.apply {
            hint = getString(R.string.search_hint)
            maxLines = 1
            inputType = EditorInfo.TYPE_CLASS_TEXT
            imeOptions = EditorInfo.IME_ACTION_DONE
            filters = arrayOf(InputFilter.LengthFilter(15))
        }

        searchEditText.requestFocus()

        searchBackButton.setNavigationOnClickListener {
            finish()
        }

        searchClearButton.setOnClickListener {
            searchEditText.text.clear()
            searchClearButton.visibility = ImageButton.GONE
            val imm = getSystemService(
                INPUT_METHOD_SERVICE
            ) as android.view.inputmethod.InputMethodManager
            imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Не используется
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                searchText = s?.toString() ?: ""

                searchClearButton.isVisible = !s.isNullOrEmpty()
                // Заглушка для будущей логики поиска
                // TODO: добавить логику поиска здесь
            }

            override fun afterTextChanged(s: Editable?) {
                // Не используется
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("SEARCH_TEXT", searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val restoredText = savedInstanceState.getString(SEARCH_TEXT, "")
        val searchEditText = findViewById<EditText>(R.id.search_edit_text)
        searchEditText.setText(restoredText)

        searchEditText.setSelection(restoredText.length)

        searchText = restoredText

        val searchClearButton = findViewById<ImageButton>(R.id.search_clear_button)
        searchClearButton.isVisible =  restoredText.isNotEmpty()
    }

    companion object {
        private const val SEARCH_TEXT = "SEARCH_TEXT"
    }
}
