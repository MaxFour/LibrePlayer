package com.maxfour.music.activities

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.SubMenu
import android.view.View
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.maxfour.appthemehelper.ThemeStore
import com.maxfour.appthemehelper.util.ATHUtil
import com.maxfour.appthemehelper.util.ColorUtil
import com.maxfour.appthemehelper.util.MaterialUtil
import com.maxfour.music.App
import com.maxfour.music.R
import com.maxfour.music.activities.base.AbsSlidingMusicPanelActivity
import com.maxfour.music.activities.tageditor.AbsTagEditorActivity
import com.maxfour.music.activities.tageditor.AlbumTagEditorActivity
import com.maxfour.music.adapter.album.HorizontalAlbumAdapter
import com.maxfour.music.adapter.song.SimpleSongAdapter
import com.maxfour.music.dialogs.AddToPlaylistDialog
import com.maxfour.music.dialogs.DeleteSongsDialog
import com.maxfour.music.extensions.show
import com.maxfour.music.glide.ArtistGlideRequest
import com.maxfour.music.glide.MusicPlayerColoredTarget
import com.maxfour.music.glide.SongGlideRequest
import com.maxfour.music.helper.MusicPlayerRemote
import com.maxfour.music.helper.SortOrder.AlbumSongSortOrder
import com.maxfour.music.model.Album
import com.maxfour.music.model.Artist
import com.maxfour.music.mvp.presenter.AlbumDetailsPresenter
import com.maxfour.music.mvp.presenter.AlbumDetailsView
import com.maxfour.music.util.MusicUtil
import com.maxfour.music.util.NavigationUtil
import com.maxfour.music.util.PreferenceUtil
import kotlinx.android.synthetic.main.activity_album.*
import kotlinx.android.synthetic.main.activity_album_content.*
import java.util.*
import javax.inject.Inject
import android.util.Pair as UtilPair

class AlbumDetailsActivity : AbsSlidingMusicPanelActivity(), AlbumDetailsView {

	private lateinit var simpleSongAdapter: SimpleSongAdapter
	private lateinit var album: Album
	private lateinit var artistImage: ImageView
	private val savedSortOrder: String
		get() = PreferenceUtil.getInstance(this).albumDetailSongSortOrder

	override fun createContentView(): View {
		return wrapSlidingMusicPanel(R.layout.activity_album)
	}

	@Inject
	lateinit var albumDetailsPresenter: AlbumDetailsPresenter

	override fun onCreate(savedInstanceState: Bundle?) {
		setDrawUnderStatusBar()
		super.onCreate(savedInstanceState)
		toggleBottomNavigationView(true)
		setStatusbarColor(Color.TRANSPARENT)
		setNavigationbarColorAuto()
		setTaskDescriptionColorAuto()
		setLightNavigationBar(true)
		setLightStatusbar(ColorUtil.isColorLight(ATHUtil.resolveColor(this, R.attr.colorPrimary)))

		ActivityCompat.postponeEnterTransition(this)

		App.musicComponent.inject(this)
		artistImage = findViewById(R.id.artistImage)

		setupRecyclerView()

		artistImage.setOnClickListener {
			val artistPairs = ActivityOptions.makeSceneTransitionAnimation(
					this,
					UtilPair.create(
							artistImage,
							getString(R.string.transition_artist_image)
					)
			)
			NavigationUtil.goToArtistOptions(this, album.artistId, artistPairs)
		}
		playAction.apply {
			setOnClickListener { MusicPlayerRemote.openQueue(album.songs!!, 0, true) }
		}
		shuffleAction.apply {
			setOnClickListener { MusicPlayerRemote.openAndShuffleQueue(album.songs!!, true) }
		}


		albumDetailsPresenter.attachView(this)

		if (intent.extras!!.containsKey(EXTRA_ALBUM_ID)) {
			intent.extras?.getInt(EXTRA_ALBUM_ID)?.let { albumDetailsPresenter.loadAlbum(it) }
		} else {
			finish()
		}
	}

	private fun setupRecyclerView() {
		simpleSongAdapter = SimpleSongAdapter(this, ArrayList(), R.layout.item_song)
		recyclerView.apply {
			layoutManager = LinearLayoutManager(this@AlbumDetailsActivity)
			itemAnimator = DefaultItemAnimator()
			isNestedScrollingEnabled = false
			adapter = simpleSongAdapter
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		albumDetailsPresenter.detachView()
	}

	override fun complete() {
		ActivityCompat.startPostponedEnterTransition(this)
	}

	override fun album(album: Album) {

		if (album.songs!!.isEmpty()) {
			finish()
			return
		}
		this.album = album

		albumTitle.text = album.title
		if (MusicUtil.getYearString(album.year) == "-") {
			albumText.text = String.format(
					"%s • %s",
					album.artistName,
					MusicUtil.getReadableDurationString(
							MusicUtil.getTotalDuration(
									this,
									album.songs
							)
					)
			)
		} else {
			albumText.text = String.format(
					"%s • %s • %s",
					album.artistName,
					MusicUtil.getYearString(album.year),
					MusicUtil.getReadableDurationString(
							MusicUtil.getTotalDuration(
									this,
									album.songs
							)
					)
			)
		}
		loadAlbumCover()
		simpleSongAdapter.swapDataSet(album.songs)
		albumDetailsPresenter.loadMore(album.artistId)
	}

	override fun moreAlbums(albums: ArrayList<Album>) {
		moreTitle.show()
		moreRecyclerView.show()
		moreTitle.text = String.format(getString(R.string.label_more_from), album.artistName)

		val albumAdapter = HorizontalAlbumAdapter(this, albums, false, null)
		moreRecyclerView.layoutManager = GridLayoutManager(
				this,
				1,
				GridLayoutManager.HORIZONTAL,
				false
		)
		moreRecyclerView.adapter = albumAdapter
	}

	override fun loadArtistImage(artist: Artist) {
		ArtistGlideRequest.Builder.from(Glide.with(this), artist).generatePalette(this).build()
			.dontAnimate().dontTransform().into(object : MusicPlayerColoredTarget(artistImage) {
				override fun onColorReady(color: Int) {

				}
			})

	}

	private fun loadAlbumCover() {
		SongGlideRequest.Builder.from(Glide.with(this), album.safeGetFirstSong())
			.checkIgnoreMediaStore(this).generatePalette(this).build().dontAnimate().dontTransform()
			.into(object : MusicPlayerColoredTarget(image) {
				override fun onColorReady(color: Int) {
					setColors(color)
				}
			})
	}

	private fun setColors(color: Int) {
		val themeColor = if (PreferenceUtil.getInstance(this).adaptiveColor) color
		else ThemeStore.accentColor(this)

		songTitle.setTextColor(themeColor)
		moreTitle.setTextColor(themeColor)

		val buttonColor = if (PreferenceUtil.getInstance(this).adaptiveColor) color
		else ATHUtil.resolveColor(this, R.attr.cardBackgroundColor)

		MaterialUtil.setTint(button = shuffleAction, color = buttonColor)
		MaterialUtil.setTint(button = playAction, color = buttonColor)

		toolbar.setBackgroundColor(ATHUtil.resolveColor(this, R.attr.colorPrimary))
		setSupportActionBar(toolbar)
		supportActionBar?.title = null
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.menu_album_detail, menu)
		val sortOrder = menu.findItem(R.id.action_sort_order)
		setUpSortOrderMenu(sortOrder.subMenu)
		return super.onCreateOptionsMenu(menu)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return handleSortOrderMenuItem(item)
	}

	private fun handleSortOrderMenuItem(item: MenuItem): Boolean {
		var sortOrder: String? = null
		val songs = simpleSongAdapter.dataSet
		when (item.itemId) {
			R.id.action_play_next                       -> {
				MusicPlayerRemote.playNext(songs)
				return true
			}
			R.id.action_add_to_current_playing          -> {
				MusicPlayerRemote.enqueue(songs)
				return true
			}
			R.id.action_add_to_playlist                 -> {
				AddToPlaylistDialog.create(songs).show(supportFragmentManager, "ADD_PLAYLIST")
				return true
			}
			R.id.action_delete_from_device              -> {
				DeleteSongsDialog.create(songs).show(supportFragmentManager, "DELETE_SONGS")
				return true
			}
			android.R.id.home                           -> {
				super.onBackPressed()
				return true
			}
			R.id.action_tag_editor                      -> {
				val intent = Intent(this, AlbumTagEditorActivity::class.java)
				intent.putExtra(AbsTagEditorActivity.EXTRA_ID, album.id)
				startActivityForResult(intent, TAG_EDITOR_REQUEST)
				return true
			}
			/*Sort*/
			R.id.action_sort_order_title                -> sortOrder = AlbumSongSortOrder.SONG_A_Z
			R.id.action_sort_order_title_desc           -> sortOrder = AlbumSongSortOrder.SONG_Z_A
			R.id.action_sort_order_song_list           -> sortOrder = AlbumSongSortOrder.SONG_LIST
			R.id.action_sort_order_artist_song_duration -> sortOrder = AlbumSongSortOrder.SONG_DURATION
		}
		if (sortOrder != null) {
			item.isChecked = true
			setSaveSortOrder(sortOrder)
		}
		return true
	}

	private fun setUpSortOrderMenu(sortOrder: SubMenu) {
		when (savedSortOrder) {
			AlbumSongSortOrder.SONG_A_Z        -> sortOrder.findItem(R.id.action_sort_order_title)
				.isChecked = true
			AlbumSongSortOrder.SONG_Z_A        -> sortOrder.findItem(R.id.action_sort_order_title_desc)
				.isChecked = true
			AlbumSongSortOrder.SONG_LIST -> sortOrder.findItem(R.id.action_sort_order_song_list)
				.isChecked = true
			AlbumSongSortOrder.SONG_DURATION   -> sortOrder.findItem(R.id.action_sort_order_artist_song_duration)
				.isChecked = true
		}
	}

	private fun setSaveSortOrder(sortOrder: String?) {
		PreferenceUtil.getInstance(this).albumDetailSongSortOrder = sortOrder
		reload()
	}

	override fun onMediaStoreChanged() {
		super.onMediaStoreChanged()
		reload()
	}

	private fun reload() {
		if (intent.extras!!.containsKey(EXTRA_ALBUM_ID)) {
			intent.extras?.getInt(EXTRA_ALBUM_ID)?.let { albumDetailsPresenter.loadAlbum(it) }
		} else {
			finish()
		}
	}

	companion object {

		const val EXTRA_ALBUM_ID = "extra_album_id"
		private const val TAG_EDITOR_REQUEST = 2001
	}
}