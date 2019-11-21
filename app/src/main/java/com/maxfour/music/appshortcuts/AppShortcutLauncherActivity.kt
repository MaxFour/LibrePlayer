package com.maxfour.music.appshortcuts

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.maxfour.music.activities.SearchActivity
import com.maxfour.music.appshortcuts.shortcuttype.LastAddedShortcutType
import com.maxfour.music.appshortcuts.shortcuttype.SearchShortCutType
import com.maxfour.music.appshortcuts.shortcuttype.ShuffleAllShortcutType
import com.maxfour.music.appshortcuts.shortcuttype.TopTracksShortcutType
import com.maxfour.music.model.Playlist
import com.maxfour.music.model.smartplaylist.LastAddedPlaylist
import com.maxfour.music.model.smartplaylist.MyTopTracksPlaylist
import com.maxfour.music.model.smartplaylist.ShuffleAllPlaylist
import com.maxfour.music.service.MusicService
import com.maxfour.music.service.MusicService.*

class AppShortcutLauncherActivity : Activity() {

	public override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		var shortcutType = SHORTCUT_TYPE_NONE

		// Set shortcutType from the intent extras
		val extras = intent.extras
		if (extras != null) {
			shortcutType = extras.getInt(KEY_SHORTCUT_TYPE, SHORTCUT_TYPE_NONE)
		}

		when (shortcutType) {
			SHORTCUT_TYPE_SHUFFLE_ALL -> {
				startServiceWithPlaylist(
						MusicService.SHUFFLE_MODE_SHUFFLE, ShuffleAllPlaylist(applicationContext)
				)
				DynamicShortcutManager.reportShortcutUsed(this, ShuffleAllShortcutType.id)
			}
			SHORTCUT_TYPE_TOP_TRACKS  -> {
				startServiceWithPlaylist(
						MusicService.SHUFFLE_MODE_NONE, MyTopTracksPlaylist(applicationContext)
				)
				DynamicShortcutManager.reportShortcutUsed(this, TopTracksShortcutType.id)
			}
			SHORTCUT_TYPE_LAST_ADDED  -> {
				startServiceWithPlaylist(
						MusicService.SHUFFLE_MODE_NONE, LastAddedPlaylist(applicationContext)
				)
				DynamicShortcutManager.reportShortcutUsed(this, LastAddedShortcutType.id)
			}
			SHORTCUT_TYPE_SEARCH      -> {
				startActivity(Intent(this, SearchActivity::class.java))
				DynamicShortcutManager.reportShortcutUsed(this, SearchShortCutType.id)
			}
		}
		finish()
	}

	private fun startServiceWithPlaylist(shuffleMode: Int, playlist: Playlist) {
		val intent = Intent(this, MusicService::class.java)
		intent.action = ACTION_PLAY_PLAYLIST

		val bundle = Bundle()
		bundle.putParcelable(INTENT_EXTRA_PLAYLIST, playlist)
		bundle.putInt(INTENT_EXTRA_SHUFFLE_MODE, shuffleMode)

		intent.putExtras(bundle)

		startService(intent)
	}

	companion object {
		const val KEY_SHORTCUT_TYPE = "com.maxfour.music.appshortcuts.ShortcutType"
		const val SHORTCUT_TYPE_SHUFFLE_ALL = 0
		const val SHORTCUT_TYPE_TOP_TRACKS = 1
		const val SHORTCUT_TYPE_LAST_ADDED = 2
		const val SHORTCUT_TYPE_SEARCH = 3
		const val SHORTCUT_TYPE_NONE = 4
	}
}
