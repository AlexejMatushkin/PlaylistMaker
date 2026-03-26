package com.practicum.playlistmaker

import android.app.Application

class PlaylistMakerApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Creator.init(this)

        Creator.provideThemeManager().applyCurrentTheme()
    }

}
