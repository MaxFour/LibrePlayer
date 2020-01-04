package com.maxfour.libreplayer.adapter.artist

import android.app.ActivityOptions
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.maxfour.appthemehelper.util.ColorUtil
import com.maxfour.appthemehelper.util.MaterialValueHelper
import com.maxfour.libreplayer.R
import com.maxfour.libreplayer.adapter.base.AbsMultiSelectAdapter
import com.maxfour.libreplayer.adapter.base.MediaEntryViewHolder
import com.maxfour.libreplayer.glide.ArtistGlideRequest
import com.maxfour.libreplayer.glide.PlayerColoredTarget
import com.maxfour.libreplayer.helper.menu.SongsMenuHelper
import com.maxfour.libreplayer.interfaces.CabHolder
import com.maxfour.libreplayer.model.Artist
import com.maxfour.libreplayer.model.Song
import com.maxfour.libreplayer.util.MusicUtil
import com.maxfour.libreplayer.util.NavigationUtil
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import java.util.ArrayList

class ArtistAdapter(
	val activity: AppCompatActivity,
	var dataSet: ArrayList<Artist>,
	var itemLayoutRes: Int,
	var usePalette: Boolean,
	cabHolder: CabHolder?
) : AbsMultiSelectAdapter<ArtistAdapter.ViewHolder, Artist>(
	activity, cabHolder, R.menu.menu_media_selection
), FastScrollRecyclerView.SectionedAdapter {

	fun swapDataSet(dataSet: ArrayList<Artist>) {
		this.dataSet = dataSet
		notifyDataSetChanged()
	}

	fun usePalette(usePalette: Boolean) {
		this.usePalette = usePalette
		notifyDataSetChanged()
	}

	override fun getItemId(position: Int): Long {
		return dataSet[position].id.toLong()
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val view = LayoutInflater.from(activity).inflate(itemLayoutRes, parent, false)
		return createViewHolder(view)
	}

	private fun createViewHolder(view: View): ViewHolder {
		return ViewHolder(view)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val artist = dataSet[position]
		val isChecked = isChecked(artist)
		holder.itemView.isActivated = isChecked
		holder.title?.text = artist.name
		holder.text?.visibility = View.GONE
		loadArtistImage(artist, holder)
	}

	fun setColors(color: Int, holder: ViewHolder) {
		if (holder.paletteColorContainer != null) {
			holder.paletteColorContainer?.setBackgroundColor(color)
			holder.title?.setTextColor(
					MaterialValueHelper.getPrimaryTextColor(
							activity, ColorUtil.isColorLight(
							color
							)
					)
			)
		}

		holder.mask?.backgroundTintList = ColorStateList.valueOf(color)
	}

	private fun loadArtistImage(artist: Artist, holder: ViewHolder) {
		if (holder.image == null) {
			return
		}
		ArtistGlideRequest.Builder.from(Glide.with(activity), artist).generatePalette(activity)
			.build().into(object : PlayerColoredTarget(holder.image!!) {
				override fun onLoadCleared(placeholder: Drawable?) {
					super.onLoadCleared(placeholder)
					setColors(defaultFooterColor, holder)
				}

				override fun onColorReady(color: Int) {
					setColors(color, holder)
				}
			})
	}

	override fun getItemCount(): Int {
		return dataSet.size
	}

	override fun getIdentifier(position: Int): Artist? {
		return dataSet[position]
	}

	override fun getName(artist: Artist): String {
		return artist.name
	}

	override fun onMultipleItemAction(
			menuItem: MenuItem, selection: ArrayList<Artist>
	) {
		SongsMenuHelper.handleMenuClick(activity, getSongList(selection), menuItem.itemId)
	}

	private fun getSongList(artists: List<Artist>): ArrayList<Song> {
		val songs = ArrayList<Song>()
		for (artist in artists) {
			songs.addAll(artist.songs) // maybe async in future?
		}
		return songs
	}

	override fun getSectionName(position: Int): String {
		return MusicUtil.getSectionName(dataSet[position].name)
	}

	inner class ViewHolder(itemView: View) : MediaEntryViewHolder(itemView) {

		init {
			setImageTransitionName(activity.getString(R.string.transition_artist_image))
			menu?.visibility = View.GONE
		}

		override fun onClick(v: View?) {
			super.onClick(v)
			if (isInQuickSelectMode) {
				toggleChecked(adapterPosition)
			} else {
				val activityOptions = ActivityOptions.makeSceneTransitionAnimation(
						activity,
						imageContainerCard ?: image,
						"${activity.getString(R.string.transition_artist_image)}_${dataSet[adapterPosition].id}"
				)
				NavigationUtil.goToArtistOptions(
						activity, dataSet[adapterPosition].id, activityOptions
				)
			}
		}

        override fun onLongClick(v: View?): Boolean {
			toggleChecked(adapterPosition)
			return super.onLongClick(v)
		}
	}
}
