package com.maxfour.music.activities

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialcab.MaterialCab
import com.maxfour.appthemehelper.util.ATHUtil
import com.maxfour.appthemehelper.util.ColorUtil
import com.maxfour.music.App
import com.maxfour.music.R
import com.maxfour.music.activities.base.AbsSlidingMusicPanelActivity
import com.maxfour.music.adapter.song.ShuffleButtonSongAdapter
import com.maxfour.music.extensions.applyToolbar
import com.maxfour.music.helper.menu.GenreMenuHelper
import com.maxfour.music.interfaces.CabHolder
import com.maxfour.music.model.Genre
import com.maxfour.music.model.Song
import com.maxfour.music.mvp.presenter.GenreDetailsPresenter
import com.maxfour.music.mvp.presenter.GenreDetailsView
import com.maxfour.music.util.MusicColorUtil
import com.maxfour.music.util.ViewUtil
import kotlinx.android.synthetic.main.activity_playlist_detail.*
import java.util.*
import javax.inject.Inject

class GenreDetailsActivity : AbsSlidingMusicPanelActivity(), CabHolder, GenreDetailsView {

	@Inject
	lateinit var genreDetailsPresenter: GenreDetailsPresenter

	private lateinit var genre: Genre
	private lateinit var songAdapter: ShuffleButtonSongAdapter
	private var cab: MaterialCab? = null

	private fun checkIsEmpty() {
		empty?.visibility = if (songAdapter.itemCount == 0) View.VISIBLE else View.GONE
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		setDrawUnderStatusBar()
		super.onCreate(savedInstanceState)

		setStatusbarColor(Color.TRANSPARENT)
		setNavigationbarColorAuto()
		setTaskDescriptionColorAuto()
		setLightNavigationBar(true)
		setLightStatusbar(ColorUtil.isColorLight(ATHUtil.resolveColor(this, R.attr.colorPrimary)))
		toggleBottomNavigationView(true)

		if (intent.extras != null) {
			genre = intent?.extras?.getParcelable(EXTRA_GENRE_ID)!!
		} else {
			finish()
		}

		setUpToolBar()
		setupRecyclerView()

		App.musicComponent.inject(this)
		genreDetailsPresenter.attachView(this)

	}

	private fun setUpToolBar() {
		val primaryColor = ATHUtil.resolveColor(this, R.attr.colorPrimary)
		appBarLayout.setBackgroundColor(primaryColor)
		applyToolbar(toolbar)
		title = genre.name
	}

	override fun onResume() {
		super.onResume()
		genreDetailsPresenter.loadGenreSongs(genre.id)
	}

	override fun onDestroy() {
		super.onDestroy()
		genreDetailsPresenter.detachView()
	}

	override fun createContentView(): View {
		return wrapSlidingMusicPanel(R.layout.activity_playlist_detail)
	}

	override fun showEmptyView() {

	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.menu_genre_detail, menu)
		return super.onCreateOptionsMenu(menu)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		if (item.itemId == android.R.id.home) {
			onBackPressed()
		}
		return GenreMenuHelper.handleMenuClick(this, genre, item)
	}

	private fun setupRecyclerView() {
		ViewUtil.setUpFastScrollRecyclerViewColor(this, recyclerView)
		songAdapter = ShuffleButtonSongAdapter(this, ArrayList(), R.layout.item_list, false, this)
		recyclerView.apply {
			itemAnimator = DefaultItemAnimator()
			layoutManager = LinearLayoutManager(this@GenreDetailsActivity)
			adapter = songAdapter
		}
		songAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
			override fun onChanged() {
				super.onChanged()
				checkIsEmpty()
			}
		})
	}

	override fun songs(songs: ArrayList<Song>) {
		songAdapter.swapDataSet(songs)
	}

	override fun openCab(menuRes: Int, callback: MaterialCab.Callback): MaterialCab {
		if (cab != null && cab!!.isActive) cab!!.finish()
		cab = MaterialCab(this, R.id.cab_stub).setMenu(menuRes)
			.setCloseDrawableRes(R.drawable.ic_close_white_24dp).setBackgroundColor(
						MusicColorUtil.shiftBackgroundColorForLightText(
							ATHUtil.resolveColor(
									this,
									R.attr.colorPrimary
							)
					)
			).start(callback)
		return cab!!
	}

	override fun onBackPressed() {
		if (cab != null && cab!!.isActive) cab!!.finish()
		else {
			recyclerView!!.stopScroll()
			super.onBackPressed()
		}
	}

	override fun onMediaStoreChanged() {
		super.onMediaStoreChanged()
		genreDetailsPresenter.loadGenreSongs(genre.id)
	}

	companion object {
		const val EXTRA_GENRE_ID = "extra_genre_id"
	}
}
