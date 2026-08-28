package com.practicum.playlistmaker.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.ui.common.components.ScreenToolbar
import com.practicum.playlistmaker.ui.settings.view_model.SettingsViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val isDarkTheme by viewModel.themeState.observeAsState(false)

    val shareAppName = stringResource(R.string.share_app_name)
    val shareAppText = stringResource(R.string.share_app_text)
    val supportEmail = stringResource(R.string.support_email)
    val supportSubject = stringResource(R.string.support_subject)
    val supportBody = stringResource(R.string.support_body)
    val termsUrl = stringResource(R.string.user_agreement_url)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .safeDrawingPadding()
    ) {
        ScreenToolbar(title = stringResource(R.string.settings))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.dark_theme_name),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = isDarkTheme,
                onCheckedChange = { viewModel.switchTheme(it) }
            )
        }

        SettingsRow(
            text = shareAppName,
            iconRes = R.drawable.ic_share_24,
            onClick = { viewModel.shareApp(shareAppText, shareAppName) }
        )

        SettingsRow(
            text = stringResource(R.string.write_support_name),
            iconRes = R.drawable.ic_support_24,
            onClick = { viewModel.sendSupport(supportEmail, supportSubject, supportBody) }
        )

        SettingsRow(
            text = stringResource(R.string.user_agreement_name),
            iconRes = R.drawable.ic_arrow_forward_24,
            onClick = { viewModel.openAgreement(termsUrl) }
        )
    }
}

@Composable
private fun SettingsRow(
    text: String,
    iconRes: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}