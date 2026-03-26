package com.practicum.playlistmaker.presentation.ui.settings

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.presentation.viewmodel.SettingsViewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var viewModel: SettingsViewModel
    private var isUpdatingFromViewModel = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

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
                Creator.provideSettingsInteractor(),
                Creator.provideThemeManager()
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

        viewModel.shareEvent.observe(this) {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_text))
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_app_name)))
        }

        viewModel.supportEvent.observe(this) {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = "mailto:".toUri()
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.support_body))
            }
            startActivity(emailIntent)
        }

        viewModel.agreementEvent.observe(this) {
            val agreementUri = getString(R.string.user_agreement_url).toUri()
            val browserIntent = Intent(Intent.ACTION_VIEW, agreementUri)
            startActivity(browserIntent)
        }
    }
}
