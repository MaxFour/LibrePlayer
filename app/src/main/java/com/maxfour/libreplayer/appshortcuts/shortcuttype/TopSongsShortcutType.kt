package com.maxfour.libreplayer.appshortcuts.shortcuttype

import android.annotation.TargetApi
import android.content.Context
import android.content.pm.ShortcutInfo
import android.os.Build
import com.maxfour.libreplayer.R
import com.maxfour.libreplayer.appshortcuts.AppShortcutIconGenerator
import com.maxfour.libreplayer.appshortcuts.AppShortcutLauncherActivity

@TargetApi(Build.VERSION_CODES.N_MR1)
class TopSongsShortcutType(context: Context) : BaseShortcutType(context) {

	override val shortcutInfo: ShortcutInfo
		get() = ShortcutInfo.Builder(
				context, id
		).setShortLabel(context.getString(R.string.app_shortcut_top_songs_short)).setLongLabel(
				context.getString(R.string.app_shortcut_top_songs_long)
		).setIcon(
				AppShortcutIconGenerator.generateThemedIcon(
						context, R.drawable.ic_app_shortcut_top_songs
				)
		).setIntent(getPlaySongsIntent(AppShortcutLauncherActivity.SHORTCUT_TYPE_TOP_SONGS)).build()

	companion object {

		val id: String
			get() = BaseShortcutType.ID_PREFIX + "top_songs"
	}
}
