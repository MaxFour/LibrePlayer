package com.maxfour.music.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
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
import com.maxfour.music.adapter.album.AlbumAdapter
import com.maxfour.music.adapter.album.HorizontalAlbumAdapter
import com.maxfour.music.adapter.song.SimpleSongAdapter
import com.maxfour.music.dialogs.AddToPlaylistDialog
import com.maxfour.music.glide.ArtistGlideRequest
import com.maxfour.music.glide.MusicPlayerColoredTarget
import com.maxfour.music.helper.MusicPlayerRemote
import com.maxfour.music.model.Artist
import com.maxfour.music.mvp.presenter.ArtistDetailsPresenter
import com.maxfour.music.mvp.presenter.ArtistDetailsView
import com.maxfour.music.rest.LastFMRestClient
import com.maxfour.music.rest.model.LastFmArtist
import com.maxfour.music.util.*
import kotlinx.android.synthetic.main.activity_artist_content.*
import kotlinx.android.synthetic.main.activity_artist_details.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class ArtistDetailActivity : AbsSlidingMusicPanelActivity(), ArtistDetailsView {

	private var biography: Spanned? = null
	private lateinit var artist: Artist
	private var lastFMRestClient: LastFMRestClient? = null
	private lateinit var songAdapter: SimpleSongAdapter
	private lateinit var albumAdapter: AlbumAdapter
	private var forceDownload: Boolean = false

	override fun createContentView(): View {
		return wrapSlidingMusicPanel(R.layout.activity_artist_details)
	}

	@Inject
	lateinit var artistDetailsPresenter: ArtistDetailsPresenter

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

		lastFMRestClient = LastFMRestClient(this)

		setUpViews()

		playAction.apply {
			setOnClickListener { MusicPlayerRemote.openQueue(artist.songs, 0, true) }
		}
		shuffleAction.apply {
			setOnClickListener { MusicPlayerRemote.openAndShuffleQueue(artist.songs, true) }
		}

		biographyText.setOnClickListener {
			if (biographyText.maxLines == 4) {
				biographyText.maxLines = Integer.MAX_VALUE
			} else {
				biographyText.maxLines = 4
			}
		}

		App.musicComponent.inject(this)
		artistDetailsPresenter.attachView(this)

		if (intent.extras!!.containsKey(EXTRA_ARTIST_ID)) {
			intent.extras?.getInt(EXTRA_ARTIST_ID)?.let { artistDetailsPresenter.loadArtist(it) }
		} else {
			finish()
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		artistDetailsPresenter.detachView()
	}

	private fun setUpViews() {
		setupRecyclerView()
		setupContainerHeight()
	}

	private fun setupContainerHeight() {
		imageContainer?.let {
			val params = it.layoutParams
			params.width = DensityUtil.getScreenHeight(this) / 2
			it.layoutParams = params
		}
	}

	private fun setupRecyclerView() {
		albumAdapter = HorizontalAlbumAdapter(this, ArrayList(), false, null)
		albumRecyclerView.apply {
			itemAnimator = DefaultItemAnimator()
			layoutManager = GridLayoutManager(this.context, 1, GridLayoutManager.HORIZONTAL, false)
			adapter = albumAdapter
		}
		songAdapter = SimpleSongAdapter(this, ArrayList(), R.layout.item_song)
		recyclerView.apply {
			itemAnimator = DefaultItemAnimator()
			layoutManager = LinearLayoutManager(this.context)
			adapter = songAdapter
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		when (requestCode) {
			REQUEST_CODE_SELECT_IMAGE -> if (resultCode == Activity.RESULT_OK) {
				data?.data?.let {
					CustomArtistImageUtil.getInstance(this).setCustomArtistImage(artist, it)
				}
			}
			else                      -> if (resultCode == Activity.RESULT_OK) {
				reload()
			}
		}
	}

	override fun showEmptyView() {

	}

	override fun complete() {
		ActivityCompat.startPostponedEnterTransition(this)
	}

	override fun artist(artist: Artist) {
		if (artist.songCount <= 0) {
			finish()
		}
		this.artist = artist
		loadArtistImage()

		if (MusicPlayerUtil.isAllowedToDownloadMetadata(this)) {
			loadBiography(artist.name)
		}
		artistTitle.text = artist.name
		text.text = String.format(
				"%s • %s",
				MusicUtil.getArtistInfoString(this, artist),
				MusicUtil.getReadableDurationString(MusicUtil.getTotalDuration(this, artist.songs))
		)

		songAdapter.swapDataSet(artist.songs)
		albumAdapter.swapDataSet(artist.albums!!)
	}

	private fun loadBiography(
			name: String, lang: String? = Locale.getDefault().language
	) {
		biography = null
		this.lang = lang
		artistDetailsPresenter.loadBiography(name, lang, null)
	}

	override fun artistInfo(lastFmArtist: LastFmArtist?) {
		if (lastFmArtist != null && lastFmArtist.artist != null) {
			val bioContent = lastFmArtist.artist.bio.content
			if (bioContent != null && bioContent.trim { it <= ' ' }.isNotEmpty()) {
				biographyText.visibility = View.VISIBLE
				biographyTitle.visibility = View.VISIBLE
				biography = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
					Html.fromHtml(bioContent, Html.FROM_HTML_MODE_LEGACY)
				} else {
					Html.fromHtml(bioContent)
				}
				biographyText.text = biography
			}
		}

		// If the "lang" parameter is set and no biography is given, retry with default language
		if (biography == null && lang != null) {
			loadBiography(artist.name, null)
		}
	}

	private var lang: String? = null

	private fun loadArtistImage() {
		ArtistGlideRequest.Builder.from(Glide.with(this), artist).generatePalette(this).build()
			.dontAnimate().into(object : MusicPlayerColoredTarget(artistImage) {
				override fun onColorReady(color: Int) {
					setColors(color)
				}
			})
	}

	private fun setColors(color: Int) {

		val textColor = if (PreferenceUtil.getInstance(this).adaptiveColor) color
		else ThemeStore.accentColor(this)

		albumTitle.setTextColor(textColor)
		songTitle.setTextColor(textColor)
		biographyTitle.setTextColor(textColor)

		val buttonColor = if (PreferenceUtil.getInstance(this).adaptiveColor) color
		else ATHUtil.resolveColor(this, R.attr.cardBackgroundColor)

		MaterialUtil.setTint(button = shuffleAction, color = buttonColor)
		MaterialUtil.setTint(button = playAction, color = buttonColor)

		toolbar.setBackgroundColor(ATHUtil.resolveColor(this, R.attr.colorPrimary))
		setSupportActionBar(toolbar)
		supportActionBar?.title = null
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return handleSortOrderMenuItem(item)
	}

	private fun handleSortOrderMenuItem(item: MenuItem): Boolean {
		val songs = artist.songs
		when (item.itemId) {
			android.R.id.home                  -> {
				super.onBackPressed()
				return true
			}
			R.id.action_play_next              -> {
				MusicPlayerRemote.playNext(songs)
				return true
			}
			R.id.action_add_to_current_playing -> {
				MusicPlayerRemote.enqueue(songs)
				return true
			}
			R.id.action_add_to_playlist        -> {
				AddToPlaylistDialog.create(songs).show(supportFragmentManager, "ADD_PLAYLIST")
				return true
			}
			R.id.action_set_artist_image       -> {
				val intent = Intent(Intent.ACTION_GET_CONTENT)
				intent.type = "image/*"
				startActivityForResult(
						Intent.createChooser(
								intent,
								getString(R.string.pick_from_local_storage)
						), REQUEST_CODE_SELECT_IMAGE
				)
				return true
			}
			R.id.action_reset_artist_image     -> {
				Toast.makeText(
						this@ArtistDetailActivity,
						resources.getString(R.string.updating),
						Toast.LENGTH_SHORT
				).show()
				CustomArtistImageUtil.getInstance(this@ArtistDetailActivity)
					.resetCustomArtistImage(artist)
				forceDownload = true
				return true
			}
		}
		return true
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.menu_artist_detail, menu)
		return super.onCreateOptionsMenu(menu)
	}

	override fun onMediaStoreChanged() {
		super.onMediaStoreChanged()
		reload()
	}

	private fun reload() {
		if (intent.extras!!.containsKey(EXTRA_ARTIST_ID)) {
			intent.extras?.getInt(EXTRA_ARTIST_ID)?.let { artistDetailsPresenter.loadArtist(it) }
		} else {
			finish()
		}
	}

	companion object {

		const val EXTRA_ARTIST_ID = "extra_artist_id"
		const val REQUEST_CODE_SELECT_IMAGE = 9003
	}
}
