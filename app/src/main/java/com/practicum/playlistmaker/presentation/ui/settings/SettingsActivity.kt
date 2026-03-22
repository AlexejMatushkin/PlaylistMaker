package com.practicum.playlistmaker.presentation.ui.settings

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.PlaylistMakerApp
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.api.SettingsInteractor

class SettingsActivity : AppCompatActivity() {

    private lateinit var settingsInteractor: SettingsInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        settingsInteractor = (application as PlaylistMakerApp).let { app ->
            Creator.provideSettingsInteractor()
        }

        setupToolbar()
        setupShareButton()
        setupSupportButton()
        setupAgreementButton()
        setupThemeSwitcher()
    }

    private fun setupToolbar() {
        findViewById<MaterialToolbar>(R.id.back_toolbar_settings).setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupShareButton() {
        findViewById<MaterialTextView>(R.id.share_button).setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_text))
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_app_name)))
        }
    }

    private fun setupSupportButton() {
        findViewById<MaterialTextView>(R.id.support_button).setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = "mailto:".toUri()
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.support_body))
            }
            startActivity(emailIntent)
        }
    }

    private fun setupAgreementButton() {
        findViewById<MaterialTextView>(R.id.agreement_button).setOnClickListener {
            val agreementUri = getString(R.string.user_agreement_url).toUri()
            val browserIntent = Intent(Intent.ACTION_VIEW, agreementUri)
            startActivity(browserIntent)
        }
    }

    private fun setupThemeSwitcher() {
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.theme_switch)

        val themeSettings = settingsInteractor.getThemeSettings()
        themeSwitcher.isChecked = themeSettings.isDarkTheme

        themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            (application as PlaylistMakerApp).switchTheme(isChecked)
        }
    }
}
