package com.maxfour.music.appwidgets

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.View
import android.widget.RemoteViews
import com.bumptech.glide.Glide
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.maxfour.appthemehelper.util.MaterialValueHelper
import com.maxfour.music.R
import com.maxfour.music.activities.MainActivity
import com.maxfour.music.appwidgets.base.BaseAppWidget
import com.maxfour.music.glide.SongGlideRequest
import com.maxfour.music.service.MusicService
import com.maxfour.music.service.MusicService.*
import com.maxfour.music.util.MusicPlayerUtil

class AppWidgetBig : BaseAppWidget() {
	private var target: Target<Bitmap>? = null // for cancellation

	/**
	 * Initialize given widgets to default state, where we launch Music on default click and hide
	 * actions if service not running.
	 */
	override fun defaultAppWidget(context: Context, appWidgetIds: IntArray) {
		val appWidgetView = RemoteViews(
				context.packageName, com.maxfour.music.R.layout.app_widget_big
		)

		appWidgetView.setViewVisibility(
				com.maxfour.music.R.id.media_titles,
				View.INVISIBLE
		)
		appWidgetView.setImageViewResource(R.id.image, R.drawable.default_album_art)
		appWidgetView.setImageViewBitmap(
				R.id.button_next, BaseAppWidget.createBitmap(
				MusicPlayerUtil.getTintedVectorDrawable(
						context,
						com.maxfour.music.R.drawable.ic_skip_next_white_24dp,
						MaterialValueHelper.getPrimaryTextColor(context, false)
				)!!, 1f
		)
		)
		appWidgetView.setImageViewBitmap(
				R.id.button_prev, BaseAppWidget.Companion.createBitmap(
				MusicPlayerUtil.getTintedVectorDrawable(
						context,
						com.maxfour.music.R.drawable.ic_skip_previous_white_24dp,
						MaterialValueHelper.getPrimaryTextColor(context, false)
				)!!, 1f
		)
		)
		appWidgetView.setImageViewBitmap(
				R.id.button_toggle_play_pause, BaseAppWidget.Companion.createBitmap(
				MusicPlayerUtil.getTintedVectorDrawable(
						context,
						com.maxfour.music.R.drawable.ic_play_arrow_white_32dp,
						MaterialValueHelper.getPrimaryTextColor(context, false)
				)!!, 1f
		)
		)

		linkButtons(context, appWidgetView)
		pushUpdate(context, appWidgetIds, appWidgetView)
	}

	/**
	 * Update all active widget instances by pushing changes
	 */
	override fun performUpdate(service: MusicService, appWidgetIds: IntArray?) {
		val appWidgetView = RemoteViews(
				service.packageName, com.maxfour.music.R.layout.app_widget_big
		)

		val isPlaying = service.isPlaying
		val song = service.currentSong

		// Set the titles and artwork
		if (TextUtils.isEmpty(song.title) && TextUtils.isEmpty(song.artistName)) {
			appWidgetView.setViewVisibility(
					com.maxfour.music.R.id.media_titles,
					View.INVISIBLE
			)
		} else {
			appWidgetView.setViewVisibility(
					com.maxfour.music.R.id.media_titles,
					View.VISIBLE
			)
			appWidgetView.setTextViewText(com.maxfour.music.R.id.title, song.title)
			appWidgetView.setTextViewText(
					com.maxfour.music.R.id.text,
					getSongArtistAndAlbum(song)
			)
		}

		// Set correct drawable for pause state
		val playPauseRes = if (isPlaying) com.maxfour.music.R.drawable.ic_pause_white_24dp else com.maxfour.music.R.drawable.ic_play_arrow_white_32dp
		appWidgetView.setImageViewBitmap(
				R.id.button_toggle_play_pause, BaseAppWidget.createBitmap(
				MusicPlayerUtil.getTintedVectorDrawable(
						service,
						playPauseRes,
						MaterialValueHelper.getPrimaryTextColor(service, false)
				)!!, 1f
		)
		)

		// Set prev/next button drawables
		appWidgetView.setImageViewBitmap(
				R.id.button_next, BaseAppWidget.Companion.createBitmap(
				MusicPlayerUtil.getTintedVectorDrawable(
						service,
						com.maxfour.music.R.drawable.ic_skip_next_white_24dp,
						MaterialValueHelper.getPrimaryTextColor(service, false)
				)!!, 1f
		)
		)
		appWidgetView.setImageViewBitmap(
				R.id.button_prev, BaseAppWidget.Companion.createBitmap(
				MusicPlayerUtil.getTintedVectorDrawable(
						service,
						com.maxfour.music.R.drawable.ic_skip_previous_white_24dp,
						MaterialValueHelper.getPrimaryTextColor(service, false)
				)!!, 1f
		)
		)

		// Link actions buttons to intents
		linkButtons(service, appWidgetView)

		// Load the album cover async and push the update on completion
		val p = MusicPlayerUtil.getScreenSize(service)
		val widgetImageSize = Math.min(p.x, p.y)
		val appContext = service.applicationContext
		service.runOnUiThread {
			if (target != null) {
				Glide.clear(target)
			}
			target = SongGlideRequest.Builder.from(Glide.with(appContext), song)
				.checkIgnoreMediaStore(appContext).asBitmap().build()
				.into(object : SimpleTarget<Bitmap>(widgetImageSize, widgetImageSize) {
					override fun onResourceReady(
							resource: Bitmap,
							glideAnimation: GlideAnimation<in Bitmap>
					) {
						update(resource)
					}

					override fun onLoadFailed(e: Exception?, errorDrawable: Drawable?) {
						super.onLoadFailed(e, errorDrawable)
						update(null)
					}

					private fun update(bitmap: Bitmap?) {
						if (bitmap == null) {
							appWidgetView.setImageViewResource(
									R.id.image,
									R.drawable.default_album_art
							)
						} else {
							appWidgetView.setImageViewBitmap(R.id.image, bitmap)
						}
						pushUpdate(appContext, appWidgetIds, appWidgetView)
					}
				});
		}
	}

	/**
	 * Link up various button actions using [PendingIntent].
	 */
	private fun linkButtons(context: Context, views: RemoteViews) {
		val action = Intent(context, MainActivity::class.java).putExtra("expand", true)
		var pendingIntent: PendingIntent

		val serviceName = ComponentName(context, MusicService::class.java)

		// Home
		action.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
		pendingIntent = PendingIntent.getActivity(context, 0, action, 0)
		views.setOnClickPendingIntent(R.id.clickable_area, pendingIntent)

		// Previous track
		pendingIntent = buildPendingIntent(context, ACTION_REWIND, serviceName)
		views.setOnClickPendingIntent(R.id.button_prev, pendingIntent)

		// Play and pause
		pendingIntent = buildPendingIntent(context, ACTION_TOGGLE_PAUSE, serviceName)
		views.setOnClickPendingIntent(R.id.button_toggle_play_pause, pendingIntent)

		// Next track
		pendingIntent = buildPendingIntent(context, ACTION_SKIP, serviceName)
		views.setOnClickPendingIntent(R.id.button_next, pendingIntent)


	}

	companion object {

		const val NAME: String = "app_widget_big"
		private var mInstance: AppWidgetBig? = null

		val instance: AppWidgetBig
			@Synchronized get() {
				if (mInstance == null) {
					mInstance = AppWidgetBig()
				}
				return mInstance!!
			}

	}
}
