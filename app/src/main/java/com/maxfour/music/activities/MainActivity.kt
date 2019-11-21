package com.maxfour.music.activities

import android.content.*
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.maxfour.music.R
import com.maxfour.music.activities.base.AbsSlidingMusicPanelActivity
import com.maxfour.music.fragments.mainactivity.LibraryFragment
import com.maxfour.music.fragments.mainactivity.folders.FoldersFragment
import com.maxfour.music.fragments.mainactivity.home.BannerHomeFragment
import com.maxfour.music.helper.MusicPlayerRemote
import com.maxfour.music.helper.SearchQueryHelper
import com.maxfour.music.interfaces.MainActivityFragmentCallbacks
import com.maxfour.music.loaders.AlbumLoader
import com.maxfour.music.loaders.ArtistLoader
import com.maxfour.music.loaders.PlaylistSongsLoader
import com.maxfour.music.service.MusicService
import com.maxfour.music.util.PreferenceUtil
import io.reactivex.disposables.CompositeDisposable
import java.util.*

class MainActivity : AbsSlidingMusicPanelActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

	private lateinit var currentFragment: MainActivityFragmentCallbacks

	private var blockRequestPermissions: Boolean = false
	private val disposable = CompositeDisposable()
	private val broadcastReceiver = object : BroadcastReceiver() {
		override fun onReceive(context: Context, intent: Intent) {
			val action = intent.action
			if (action != null && action == Intent.ACTION_SCREEN_OFF) {
				if (PreferenceUtil.getInstance(this@MainActivity).lockScreen && MusicPlayerRemote.isPlaying) {
					val activity = Intent(context, LockScreenActivity::class.java)
					activity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
					activity.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
					ActivityCompat.startActivity(context, activity, null)
				}
			}
		}
	}

	override fun createContentView(): View {
		return wrapSlidingMusicPanel(R.layout.activity_main_content)
	}

	override fun onCreate(
			savedInstanceState: Bundle?
	) {
		setDrawUnderStatusBar()
		super.onCreate(savedInstanceState)

		getBottomNavigationView().selectedItemId = PreferenceUtil.getInstance(this).lastPage

		getBottomNavigationView().setOnNavigationItemSelectedListener {
			PreferenceUtil.getInstance(this).lastPage = it.itemId
			selectedFragment(it.itemId)
			true
		}

		if (savedInstanceState == null) {
			setMusicChooser(PreferenceUtil.getInstance(this).lastMusicChooser)
		} else {
			restoreCurrentFragment()
		}

	}

	override fun onResume() {
		super.onResume()
		val screenOnOff = IntentFilter()
		screenOnOff.addAction(Intent.ACTION_SCREEN_OFF)
		registerReceiver(broadcastReceiver, screenOnOff)

		PreferenceUtil.getInstance(this).registerOnSharedPreferenceChangedListener(this)

		if (intent.hasExtra("expand")) {
			if (intent.getBooleanExtra("expand", false)) {
				expandPanel()
				intent.putExtra("expand", false)
			}
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		disposable.clear()
		unregisterReceiver(broadcastReceiver)
		PreferenceUtil.getInstance(this).unregisterOnSharedPreferenceChangedListener(this)
	}

	private fun setCurrentFragment(fragment: Fragment, tag: String) {
		println("setCurrentFragment -> $tag -> ${supportFragmentManager.findFragmentById(R.id.fragment_container)?.tag}")
		if (tag != supportFragmentManager.findFragmentById(R.id.fragment_container)?.tag) {
			supportFragmentManager.beginTransaction()
				.replace(R.id.fragment_container, fragment, tag).commit()
			currentFragment = fragment as MainActivityFragmentCallbacks
		}
	}

	private fun restoreCurrentFragment() {
		currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as MainActivityFragmentCallbacks
	}

	private fun handlePlaybackIntent(intent: Intent?) {
		if (intent == null) {
			return
		}

		val uri = intent.data
		val mimeType = intent.type
		var handled = false
		if (intent.action != null && intent.action == MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH) {
			val songs = SearchQueryHelper.getSongs(this, intent.extras!!)
			if (MusicPlayerRemote.shuffleMode == MusicService.SHUFFLE_MODE_SHUFFLE) {
				MusicPlayerRemote.openAndShuffleQueue(songs, true)
			} else {
				MusicPlayerRemote.openQueue(songs, 0, true)
			}
			handled = true
		}

		if (uri != null && uri.toString().isNotEmpty()) {
			MusicPlayerRemote.playFromUri(uri)
			handled = true
		} else if (MediaStore.Audio.Playlists.CONTENT_TYPE == mimeType) {
			val id = parseIdFromIntent(intent, "playlistId", "playlist").toInt()
			if (id >= 0) {
				val position = intent.getIntExtra("position", 0)
				val songs = ArrayList(PlaylistSongsLoader.getPlaylistSongList(this, id))
				MusicPlayerRemote.openQueue(songs, position, true)
				handled = true
			}
		} else if (MediaStore.Audio.Albums.CONTENT_TYPE == mimeType) {
			val id = parseIdFromIntent(intent, "albumId", "album").toInt()
			if (id >= 0) {
				val position = intent.getIntExtra("position", 0)
				MusicPlayerRemote.openQueue(AlbumLoader.getAlbum(this, id).songs!!, position, true)
				handled = true
			}
		} else if (MediaStore.Audio.Artists.CONTENT_TYPE == mimeType) {
			val id = parseIdFromIntent(intent, "artistId", "artist").toInt()
			if (id >= 0) {
				val position = intent.getIntExtra("position", 0)
				MusicPlayerRemote.openQueue(ArtistLoader.getArtist(this, id).songs, position, true)
				handled = true
			}
		}
		if (handled) {
			setIntent(Intent())
		}
	}

	private fun parseIdFromIntent(intent: Intent, longKey: String, stringKey: String): Long {
		var id = intent.getLongExtra(longKey, -1)
		if (id < 0) {
			val idString = intent.getStringExtra(stringKey)
			if (idString != null) {
				try {
					id = java.lang.Long.parseLong(idString)
				} catch (e: NumberFormatException) {
					Log.e(TAG, e.message)
				}
			}
		}
		return id
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		when (requestCode) {
			APP_INTRO_REQUEST -> {
				blockRequestPermissions = false
				if (!hasPermissions()) {
					requestPermissions()
				}
			}
			REQUEST_CODE_THEME, APP_USER_INFO_REQUEST -> postRecreate()
		}

	}

	override fun handleBackPress(): Boolean {
		return super.handleBackPress() || currentFragment.handleBackPress()
	}

	override fun onServiceConnected() {
		super.onServiceConnected()
		handlePlaybackIntent(intent)
	}

	override fun requestPermissions() {
		if (!blockRequestPermissions) {
			super.requestPermissions()
		}
	}

	override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
		if (key == PreferenceUtil.GENERAL_THEME || key == PreferenceUtil.BLACK_THEME || key == PreferenceUtil.ADAPTIVE_COLOR_APP || key == PreferenceUtil.DOMINANT_COLOR || key == PreferenceUtil.USER_NAME || key == PreferenceUtil.TOGGLE_FULL_SCREEN || key == PreferenceUtil.TOGGLE_VOLUME || key == PreferenceUtil.ROUND_CORNERS || key == PreferenceUtil.CAROUSEL_EFFECT || key == PreferenceUtil.NOW_PLAYING_SCREEN_ID || key == PreferenceUtil.TOGGLE_GENRE || key == PreferenceUtil.BANNER_IMAGE_PATH || key == PreferenceUtil.PROFILE_IMAGE_PATH || key == PreferenceUtil.CIRCULAR_ALBUM_ART || key == PreferenceUtil.KEEP_SCREEN_ON || key == PreferenceUtil.TOGGLE_SEPARATE_LINE || key == PreferenceUtil.ALBUM_GRID_STYLE || key == PreferenceUtil.ARTIST_GRID_STYLE || key == PreferenceUtil.TOGGLE_HOME_BANNER || key == PreferenceUtil.TOGGLE_ADD_CONTROLS || key == PreferenceUtil.ALBUM_COVER_STYLE || key == PreferenceUtil.HOME_ARTIST_GRID_STYLE || key == PreferenceUtil.ALBUM_COVER_TRANSFORM || key == PreferenceUtil.DESATURATED_COLOR || key == PreferenceUtil.TAB_TEXT_MODE || key == PreferenceUtil.LIBRARY_CATEGORIES) postRecreate()

	}

	private fun selectedFragment(itemId: Int) {
		when (itemId) {
			R.id.action_album, R.id.action_artist, R.id.action_playlist, R.id.action_genre, R.id.action_song -> setCurrentFragment(
					LibraryFragment.newInstance(itemId),
					itemId.toString()
			)
			R.id.action_home                                                                                 -> setCurrentFragment(
					BannerHomeFragment.newInstance(),
					BannerHomeFragment.TAG
			)
			else                                                                                             -> {
				setCurrentFragment(BannerHomeFragment.newInstance(), BannerHomeFragment.TAG)
			}
		}
	}

	fun setMusicChooser(key: Int) {
		PreferenceUtil.getInstance(this).lastMusicChooser = key
		when (key) {
			FOLDER -> setCurrentFragment(FoldersFragment.newInstance(this), FoldersFragment.TAG)
			else   -> selectedFragment(PreferenceUtil.getInstance(this).lastPage)
		}
	}

	companion object {
		const val APP_INTRO_REQUEST = 2323
		const val HOME = 0
		const val FOLDER = 1
		const val LIBRARY = 2
		private const val TAG = "MainActivity"
		private const val APP_USER_INFO_REQUEST = 9003
		private const val REQUEST_CODE_THEME = 9002
	}
}
