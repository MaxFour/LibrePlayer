package com.maxfour.libreplayer.service.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import androidx.annotation.RequiresApi
import com.maxfour.libreplayer.R
import com.maxfour.libreplayer.service.MusicService

abstract class PlayingNotification {
    protected lateinit var service: MusicService
    protected var stopped: Boolean = false
    private var notifyMode = NOTIFY_MODE_BACKGROUND
    private var notificationManager: NotificationManager? = null


    @Synchronized
    fun init(service: MusicService) {
        this.service = service
        notificationManager = service.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
    }

    abstract fun update()

    @Synchronized
    fun stop() {
        stopped = true
        service.stopForeground(true)
        notificationManager!!.cancel(NOTIFICATION_ID)
    }

    internal fun updateNotifyModeAndPostNotification(notification: Notification) {
        val newNotifyMode: Int = if (service.isPlaying) {
            NOTIFY_MODE_FOREGROUND
        } else {
            NOTIFY_MODE_BACKGROUND
        }

        if (notifyMode != newNotifyMode && newNotifyMode == NOTIFY_MODE_BACKGROUND) {
            service.stopForeground(false)
        }

        if (newNotifyMode == NOTIFY_MODE_FOREGROUND) {
            service.startForeground(NOTIFICATION_ID, notification)
        } else if (newNotifyMode == NOTIFY_MODE_BACKGROUND) {
            notificationManager!!.notify(NOTIFICATION_ID, notification)
        }

        notifyMode = newNotifyMode
    }

    @RequiresApi(26)
    private fun createNotificationChannel() {
        var notificationChannel: NotificationChannel? = notificationManager!!
                .getNotificationChannel(NOTIFICATION_CHANNEL_ID)
        if (notificationChannel == null) {
            notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    service.getString(R.string.playing_notification_name),
                    NotificationManager.IMPORTANCE_LOW)
            notificationChannel.description = service.getString(R.string.playing_notification_description)
            notificationChannel.enableLights(false)
            notificationChannel.enableVibration(false)
            notificationChannel.setShowBadge(false)

            notificationManager!!.createNotificationChannel(notificationChannel)
        }
    }

    companion object {
        const val NOTIFICATION_CONTROLS_SIZE_MULTIPLIER = 1.0f
        internal const val NOTIFICATION_CHANNEL_ID = "playing_notification"
        private const val NOTIFICATION_ID = 1
        private const val NOTIFY_MODE_FOREGROUND = 1
        private const val NOTIFY_MODE_BACKGROUND = 0
    }
}
