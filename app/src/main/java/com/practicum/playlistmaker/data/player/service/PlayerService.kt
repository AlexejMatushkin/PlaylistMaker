package com.practicum.playlistmaker.data.player.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.media.PlayerController

class PlayerService : Service(), PlayerController {

    private var mediaPlayer: MediaPlayer? = null
    private var onPreparedCallback: (() -> Unit)? = null
    private var onCompletionCallback: (() -> Unit)? = null
    private var onErrorCallback: (() -> Unit)? = null
    private var isPrepared = false

    private var trackUrl: String? = null
    private var artistName: String = ""
    private var trackName: String = ""

    private val binder = PlayerBinder()

    inner class PlayerBinder : Binder() {
        fun getService(): PlayerController = this@PlayerService
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onBind(intent: Intent?): IBinder {
        trackUrl = intent?.getStringExtra(EXTRA_URL)
        artistName = intent?.getStringExtra(EXTRA_ARTIST_NAME).orEmpty()
        trackName = intent?.getStringExtra(EXTRA_TRACK_NAME).orEmpty()
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseMediaPlayer()
    }

    override fun prepare(
        url: String,
        onPrepared: () -> Unit,
        onCompletion: () -> Unit,
        onError: () -> Unit
    ) {
        releaseMediaPlayer()

        this.onPreparedCallback = onPrepared
        this.onCompletionCallback = onCompletion
        this.onErrorCallback = onError
        isPrepared = false

        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            prepareAsync()

            setOnPreparedListener {
                isPrepared = true
                this@PlayerService.onPreparedCallback?.invoke()
            }

            setOnCompletionListener {
                isPrepared = false
                this@PlayerService.onCompletionCallback?.invoke()
            }

            setOnErrorListener { _, _, _ ->
                isPrepared = false
                this@PlayerService.onErrorCallback?.invoke()
                true
            }
        }
    }

    override fun play() {
        mediaPlayer?.start()
    }

    override fun pause() {
        mediaPlayer?.pause()
    }

    override fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }

    override fun release() {
        releaseMediaPlayer()
    }

    override fun showNotification() {
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_music_note)
            .setContentTitle(getString(R.string.app_name))
            .setContentText("$artistName - $trackName")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()

        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID,
            notification,
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        )
    }

    override fun hideNotification() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Playback",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Music playback notification"
        }
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    private fun releaseMediaPlayer() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }
        mediaPlayer = null
        onPreparedCallback = null
        onCompletionCallback = null
        onErrorCallback = null
        isPrepared = false
    }

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "playback_channel"
        private const val NOTIFICATION_ID = 1
        const val EXTRA_URL = "extra_url"
        const val EXTRA_ARTIST_NAME = "extra_artist_name"
        const val EXTRA_TRACK_NAME = "extra_track_name"
    }
}
