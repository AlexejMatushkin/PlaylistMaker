package com.practicum.playlistmaker.domain.sharing

import com.practicum.playlistmaker.domain.sharing.models.EmailData

interface ExternalNavigator {
    fun shareLink(link: String)
    fun openLink(url: String)
    fun openEmail(emailData: EmailData)
}
