package com.practicum.playlistmaker.ui.mediaLibrary.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityMediaLibraryBinding
import com.practicum.playlistmaker.ui.mediaLibrary.adapter.MediaLibraryPagerAdapter
import com.practicum.playlistmaker.ui.mediaLibrary.viewModel.MediaLibraryViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaLibraryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediaLibraryBinding
    private var tabMediator: TabLayoutMediator? = null
    private val viewModel: MediaLibraryViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMediaLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.mediaLibrary) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupToolbar()
        setupTabs()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbarMedia)
        binding.toolbarMedia.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupTabs() {
        val pagerAdapter = MediaLibraryPagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter

        tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.favorite_tracks)
                1 -> getString(R.string.playlists)
                else -> ""
            }
        }
        tabMediator?.attach()
    }

    override fun onDestroy() {
        tabMediator?.detach()
        super.onDestroy()
    }
}