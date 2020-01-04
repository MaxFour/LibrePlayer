package com.maxfour.libreplayer.adapter.album

import android.app.Activity
import android.app.ActivityOptions
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.maxfour.libreplayer.R
import com.maxfour.libreplayer.glide.PlayerColoredTarget
import com.maxfour.libreplayer.glide.SongGlideRequest
import com.maxfour.libreplayer.helper.MusicPlayerRemote
import com.maxfour.libreplayer.model.Album
import com.maxfour.libreplayer.util.NavigationUtil
import com.maxfour.libreplayer.views.MetalRecyclerViewPager

class AlbumFullWidthAdapter(
	private val activity: Activity,
	private val dataSet: ArrayList<Album>,
	metrics: DisplayMetrics
) : MetalRecyclerViewPager.MetalAdapter<AlbumFullWidthAdapter.FullMetalViewHolder>(metrics) {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FullMetalViewHolder {
		return FullMetalViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.pager_item, parent, false))
	}

	override fun onBindViewHolder(holder: FullMetalViewHolder, position: Int) {
		// don't forget about calling supper.onBindViewHolder!
		super.onBindViewHolder(holder, position)
		val album = dataSet[position]
		holder.title?.text = getAlbumTitle(album)
		holder.text?.text = getAlbumText(album)
		holder.playSongs?.setOnClickListener {
			album.songs?.let { songs ->
				MusicPlayerRemote.openQueue(
						songs,
						0,
						true
				)
			}
		}
		loadAlbumCover(album, holder)
	}

	private fun getAlbumTitle(album: Album): String? {
		return album.title
	}

	private fun getAlbumText(album: Album): String? {
		return album.artistName
	}

	private fun loadAlbumCover(album: Album, holder: FullMetalViewHolder) {
		if (holder.image == null) {
			return
		}
		SongGlideRequest.Builder.from(Glide.with(activity), album.safeGetFirstSong())
			.checkIgnoreMediaStore(activity)
			.generatePalette(activity)
			.build()
			.into(object : PlayerColoredTarget(holder.image!!) {
				override fun onColorReady(color: Int) {
				}
			})
	}

	override fun getItemCount(): Int {
		return dataSet.size
	}

	inner class FullMetalViewHolder(itemView: View) : MetalRecyclerViewPager.MetalViewHolder(itemView) {

		override fun onClick(v: View?) {
			val activityOptions = ActivityOptions.makeSceneTransitionAnimation(
					activity,
					imageContainerCard ?: image,
					"${activity.getString(R.string.transition_album_art)}_${dataSet[adapterPosition].id}"
			)
			NavigationUtil.goToAlbumOptions(activity, dataSet[adapterPosition].id, activityOptions)
		}
	}
}
