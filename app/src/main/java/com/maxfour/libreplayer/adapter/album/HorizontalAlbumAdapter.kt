package com.maxfour.libreplayer.adapter.album

import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.maxfour.appthemehelper.util.ColorUtil
import com.maxfour.appthemehelper.util.MaterialValueHelper
import com.maxfour.libreplayer.glide.PlayerColoredTarget
import com.maxfour.libreplayer.glide.SongGlideRequest
import com.maxfour.libreplayer.helper.HorizontalAdapterHelper
import com.maxfour.libreplayer.interfaces.CabHolder
import com.maxfour.libreplayer.model.Album
import com.maxfour.libreplayer.util.MusicUtil
import java.util.*

class HorizontalAlbumAdapter(
		activity: AppCompatActivity,
		dataSet: ArrayList<Album>,
		usePalette: Boolean,
		cabHolder: CabHolder?
) : AlbumAdapter(
		activity, dataSet, HorizontalAdapterHelper.LAYOUT_RES, usePalette, cabHolder
) {

	override fun createViewHolder(view: View, viewType: Int): ViewHolder {
		val params = view.layoutParams as ViewGroup.MarginLayoutParams
		HorizontalAdapterHelper.applyMarginToLayoutParams(activity, params, viewType)
		return ViewHolder(view)
	}

	override fun setColors(color: Int, holder: ViewHolder) {
		holder.title?.setTextColor(MaterialValueHelper.getPrimaryTextColor(activity, ColorUtil.isColorLight(color)))
		holder.text?.setTextColor(MaterialValueHelper.getSecondaryTextColor(activity, ColorUtil.isColorLight(color)))
	}

	override fun loadAlbumCover(album: Album, holder: ViewHolder) {
		if (holder.image == null) return

		SongGlideRequest.Builder.from(Glide.with(activity), album.safeGetFirstSong())
			.checkIgnoreMediaStore(activity).generatePalette(activity).build()
			.into(object : PlayerColoredTarget(holder.image!!) {
				override fun onLoadCleared(placeholder: Drawable?) {
					super.onLoadCleared(placeholder)
					setColors(albumArtistFooterColor, holder)
				}

				override fun onColorReady(color: Int) {
					if (usePalette) setColors(color, holder)
					else setColors(albumArtistFooterColor, holder)
				}
			})
	}

	override fun getAlbumText(album: Album): String? {
		return MusicUtil.getYearString(album.year)
	}

	override fun getItemViewType(position: Int): Int {
		return HorizontalAdapterHelper.getItemViewtype(position, itemCount)
	}

	override fun getItemCount(): Int {
		return dataSet.size
	}

	companion object {
		val TAG: String = AlbumAdapter::class.java.simpleName
	}
}
