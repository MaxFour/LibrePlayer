package com.maxfour.music.service.notification

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.maxfour.appthemehelper.util.ATHUtil
import com.maxfour.appthemehelper.util.ColorUtil
import com.maxfour.appthemehelper.util.MaterialValueHelper
import com.maxfour.music.R
import com.maxfour.music.activities.MainActivity
import com.maxfour.music.glide.SongGlideRequest
import com.maxfour.music.glide.palette.BitmapPaletteWrapper
import com.maxfour.music.model.Song
import com.maxfour.music.service.MusicService
import com.maxfour.music.service.MusicService.*
import com.maxfour.music.util.MusicPlayerUtil
import com.maxfour.music.util.MusicPlayerUtil.createBitmap
import com.maxfour.music.util.PreferenceUtil
import com.maxfour.music.util.color.MediaNotificationProcessor

class PlayingNotificationOreo : PlayingNotification() {

    private var target: Target<BitmapPaletteWrapper>? = null

    private fun getCombinedRemoteViews(collapsed: Boolean, song: Song): RemoteViews {
        val remoteViews = RemoteViews(service.packageName,
                if (collapsed) R.layout.layout_notification_collapsed else R.layout.layout_notification_expanded)

        remoteViews.setTextViewText(R.id.appName, service.getString(R.string.app_name) + " • " + song.albumName)
        remoteViews.setTextViewText(R.id.title, song.title)
        remoteViews.setTextViewText(R.id.subtitle, song.artistName)

        val typedArray = service.obtainStyledAttributes(intArrayOf(android.R.attr.selectableItemBackground))
        val selectableItemBackground = typedArray.getResourceId(0, 0)
        typedArray.recycle()

        remoteViews.setInt(R.id.content, "setBackgroundResource", selectableItemBackground)

        linkButtons(remoteViews)

        //setNotificationContent(remoteViews, ColorUtil.isColorLight(backgroundColor));
        return remoteViews
    }

    override fun update() {
        stopped = false
        val song = service.currentSong
        val isPlaying = service.isPlaying

        val notificationLayout = getCombinedRemoteViews(true, song)
        val notificationLayoutBig = getCombinedRemoteViews(false, song)

        val action = Intent(service, MainActivity::class.java)
        action.putExtra("expand", true)
        action.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP

        val clickIntent = PendingIntent
                .getActivity(service, 0, action, PendingIntent.FLAG_UPDATE_CURRENT)
        val deleteIntent = buildPendingIntent(service, ACTION_QUIT, null)

        val builder = NotificationCompat.Builder(service, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(clickIntent)
                .setDeleteIntent(deleteIntent)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setCustomContentView(notificationLayout)
                .setCustomBigContentView(notificationLayoutBig)
                .setOngoing(isPlaying)

        val bigNotificationImageSize = service.resources
                .getDimensionPixelSize(R.dimen.notification_big_image_size)
        service.runOnUiThread {
            if (target != null) {
                Glide.clear(target)
            }
            target = SongGlideRequest.Builder.from(Glide.with(service), song)
                    .checkIgnoreMediaStore(service)
                    .generatePalette(service).build()
                    .centerCrop()
                    .into(object : SimpleTarget<BitmapPaletteWrapper>(bigNotificationImageSize, bigNotificationImageSize) {
                        override fun onResourceReady(resource: BitmapPaletteWrapper, glideAnimation: GlideAnimation<in BitmapPaletteWrapper>) {
                            val mediaNotificationProcessor = MediaNotificationProcessor(service, service) { i, _ -> update(resource.bitmap, i) }
                            mediaNotificationProcessor.processNotification(resource.bitmap)
                        }

                        override fun onLoadFailed(e: Exception?, errorDrawable: Drawable?) {
                            super.onLoadFailed(e, errorDrawable)
                            update(null, ATHUtil.resolveColor(service, R.attr.colorPrimary, Color.WHITE))
                        }

                        private fun update(bitmap: Bitmap?, bgColor: Int) {
                            var bgColorFinal = bgColor
                            if (bitmap != null) {
                                notificationLayout.setImageViewBitmap(R.id.largeIcon, bitmap)
                                notificationLayoutBig.setImageViewBitmap(R.id.largeIcon, bitmap)
                            } else {
                                notificationLayout.setImageViewResource(R.id.largeIcon, R.drawable.default_album_art)
                                notificationLayoutBig.setImageViewResource(R.id.largeIcon, R.drawable.default_album_art)
                            }

                            if (!PreferenceUtil.getInstance(service).coloredNotification()) {
                                bgColorFinal = ATHUtil.resolveColor(service, R.attr.colorPrimary, Color.WHITE)
                            }
                            setBackgroundColor(bgColorFinal)
                            setNotificationContent(ColorUtil.isColorLight(bgColorFinal))

                            if (stopped) {
                                return  // notification has been stopped before loading was finished
                            }
                            updateNotifyModeAndPostNotification(builder.build())
                        }

                        private fun setBackgroundColor(color: Int) {
                            notificationLayout.setInt(R.id.image, "setBackgroundColor", color)
                            notificationLayoutBig.setInt(R.id.image, "setBackgroundColor", color)
                        }

                        private fun setNotificationContent(dark: Boolean) {
                            val primary = MaterialValueHelper.getPrimaryTextColor(service, dark)
                            val secondary = MaterialValueHelper.getSecondaryTextColor(service, dark)

                            val close = createBitmap(MusicPlayerUtil.getTintedVectorDrawable(service, R.drawable.ic_close_white_24dp, primary)!!, NOTIFICATION_CONTROLS_SIZE_MULTIPLIER)
                            val prev = createBitmap(MusicPlayerUtil.getTintedVectorDrawable(service, R.drawable.ic_skip_previous_round_white_32dp, primary)!!, NOTIFICATION_CONTROLS_SIZE_MULTIPLIER)
                            val next = createBitmap(MusicPlayerUtil.getTintedVectorDrawable(service, R.drawable.ic_skip_next_round_white_32dp, primary)!!, NOTIFICATION_CONTROLS_SIZE_MULTIPLIER)
                            val playPause = createBitmap(MusicPlayerUtil.getTintedVectorDrawable(service,
                                    if (isPlaying)
                                        R.drawable.ic_pause_white_48dp
                                    else
                                        R.drawable.ic_play_arrow_white_48dp, primary)!!, NOTIFICATION_CONTROLS_SIZE_MULTIPLIER)

                            notificationLayout.setTextColor(R.id.title, primary)
                            notificationLayout.setTextColor(R.id.subtitle, secondary)
                            notificationLayout.setTextColor(R.id.appName, secondary)

                            notificationLayout.setImageViewBitmap(R.id.action_prev, prev)
                            notificationLayout.setImageViewBitmap(R.id.action_next, next)
                            notificationLayout.setImageViewBitmap(R.id.action_play_pause, playPause)

                            notificationLayoutBig.setTextColor(R.id.title, primary)
                            notificationLayoutBig.setTextColor(R.id.subtitle, secondary)
                            notificationLayoutBig.setTextColor(R.id.appName, secondary)

                            notificationLayoutBig.setImageViewBitmap(R.id.action_quit, close)
                            notificationLayoutBig.setImageViewBitmap(R.id.action_prev, prev)
                            notificationLayoutBig.setImageViewBitmap(R.id.action_next, next)
                            notificationLayoutBig.setImageViewBitmap(R.id.action_play_pause, playPause)

                            notificationLayout.setImageViewBitmap(R.id.smallIcon, createBitmap(MusicPlayerUtil.getTintedVectorDrawable(service, R.drawable.ic_notification, secondary)!!, 0.6f))
                            notificationLayoutBig.setImageViewBitmap(R.id.smallIcon, createBitmap(MusicPlayerUtil.getTintedVectorDrawable(service, R.drawable.ic_notification, secondary)!!, 0.6f))

                            notificationLayout.setInt(R.id.arrow, "setColorFilter", secondary)
                            notificationLayoutBig.setInt(R.id.arrow, "setColorFilter", secondary)

                        }
                    })
        }

        if (stopped) {
            return  // notification has been stopped before loading was finished
        }
        updateNotifyModeAndPostNotification(builder.build())
    }


    private fun buildPendingIntent(context: Context, action: String,
                                   serviceName: ComponentName?): PendingIntent {
        val intent = Intent(action)
        intent.component = serviceName
        return PendingIntent.getService(context, 0, intent, 0)
    }


    private fun linkButtons(notificationLayout: RemoteViews) {
        var pendingIntent: PendingIntent

        val serviceName = ComponentName(service, MusicService::class.java)

        // Previous track
        pendingIntent = buildPendingIntent(service, ACTION_REWIND, serviceName)
        notificationLayout.setOnClickPendingIntent(R.id.action_prev, pendingIntent)

        // Play and pause
        pendingIntent = buildPendingIntent(service, ACTION_TOGGLE_PAUSE, serviceName)
        notificationLayout.setOnClickPendingIntent(R.id.action_play_pause, pendingIntent)

        // Next track
        pendingIntent = buildPendingIntent(service, ACTION_SKIP, serviceName)
        notificationLayout.setOnClickPendingIntent(R.id.action_next, pendingIntent)

        // Close
        pendingIntent = buildPendingIntent(service, ACTION_QUIT, serviceName)
        notificationLayout.setOnClickPendingIntent(R.id.action_quit, pendingIntent)
    }

}
