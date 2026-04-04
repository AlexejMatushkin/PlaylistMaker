package com.practicum.playlistmaker.ui.settings.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private val viewModel: SettingsViewModel by viewModel()
    private lateinit var binding: ActivitySettingsBinding
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
            val shareText = getString(R.string.share_app_text)
            val shareTitle = getString(R.string.share_app_name)
            viewModel.shareApp(shareText, shareTitle)
        }
    }

    private fun setupSupportButton() {
        binding.supportButton.setOnClickListener {
            val email = getString(R.string.support_email)
            val subject = getString(R.string.support_subject)
            val body = getString(R.string.support_body)
            viewModel.sendSupport(email, subject, body)
        }
    }

    private fun setupAgreementButton() {
        binding.agreementButton.setOnClickListener {
            val termsUrl = getString(R.string.user_agreement_url)
            viewModel.openAgreement(termsUrl)
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
