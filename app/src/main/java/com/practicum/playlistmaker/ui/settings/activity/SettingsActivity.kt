package com.practicum.playlistmaker.ui.settings.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.ui.settings.view_model.SettingsViewModel
import com.practicum.playlistmaker.ui.settings.view_model.SettingsViewModelFactory

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var viewModel: SettingsViewModel
    private var isUpdatingFromViewModel = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel = ViewModelProvider(
            this,
            SettingsViewModelFactory(
                Creator.provideGetThemeSettingsInteractor(),
                Creator.provideThemeManager(),
                Creator.provideSharingInteractor()
            )
        )[SettingsViewModel::class.java]

        setupToolbar()
        setupShareButton()
        setupSupportButton()
        setupAgreementButton()
        setupThemeSwitcher()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.backToolbarSettings.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupShareButton() {
        binding.shareButton.setOnClickListener {
            viewModel.shareApp()
        }
    }

    private fun setupSupportButton() {
        binding.supportButton.setOnClickListener {
            viewModel.sendSupport()
        }
    }

    private fun setupAgreementButton() {
        binding.agreementButton.setOnClickListener {
            viewModel.openAgreement()
        }
    }

    private fun setupThemeSwitcher() {
        binding.themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (!isUpdatingFromViewModel) {
                viewModel.switchTheme(isChecked)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.themeState.observe(this) { isDarkTheme ->
            if (binding.themeSwitch.isChecked != isDarkTheme) {
                isUpdatingFromViewModel = true
                binding.themeSwitch.isChecked = isDarkTheme
                isUpdatingFromViewModel = false
            }
        }
    }
}
