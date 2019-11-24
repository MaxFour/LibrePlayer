package com.maxfour.music.adapter.song

import android.app.ActivityOptions
import android.content.res.ColorStateList
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.maxfour.appthemehelper.util.ColorUtil
import com.maxfour.appthemehelper.util.MaterialValueHelper
import com.maxfour.music.R
import com.maxfour.music.helper.MusicPlayerRemote
import com.maxfour.music.interfaces.CabHolder
import com.maxfour.music.model.Song
import com.maxfour.music.util.MusicColorUtil
import com.maxfour.music.util.NavigationUtil
import java.util.*

open class PlaylistSongAdapter(
		activity: AppCompatActivity,
		dataSet: ArrayList<Song>,
		itemLayoutRes: Int,
		usePalette: Boolean,
		cabHolder: CabHolder?
) : AbsOffsetSongAdapter(activity, dataSet, itemLayoutRes, usePalette, cabHolder, false) {

	init {
		this.setMultiSelectMenuRes(R.menu.menu_cannot_delete_single_songs_playlist_songs_selection)
	}

	override fun createViewHolder(view: View): SongAdapter.ViewHolder {
		return ViewHolder(view)
	}

	override fun onBindViewHolder(holder: SongAdapter.ViewHolder, position: Int) {

		if (holder.itemViewType == OFFSET_ITEM) {

			val buttonColor = MusicColorUtil.toolbarColor(activity)
			val textColor = MaterialValueHelper.getPrimaryTextColor(
					activity, ColorUtil.isColorLight(
					buttonColor
			)
			)
			val viewHolder = holder as ViewHolder

			viewHolder.playAction?.let {
				it.backgroundTintList = ColorStateList.valueOf(buttonColor)
				it.setTextColor(textColor)
				it.iconTint = ColorStateList.valueOf(textColor)
				it.setOnClickListener {
					MusicPlayerRemote.openQueue(dataSet, 0, true)
				}
			}
			viewHolder.shuffleAction?.let {
				it.backgroundTintList = ColorStateList.valueOf(buttonColor)
				it.setTextColor(textColor)
				it.iconTint = ColorStateList.valueOf(textColor)
				it.setOnClickListener {
					MusicPlayerRemote.openAndShuffleQueue(dataSet, true)
				}
			}

		} else {
			super.onBindViewHolder(holder, position - 1)
		}
	}

	open inner class ViewHolder(itemView: View) : AbsOffsetSongAdapter.ViewHolder(itemView) {

		val playAction: MaterialButton? = itemView.findViewById(R.id.playAction)
		val shuffleAction: MaterialButton? = itemView.findViewById(R.id.shuffleAction)

		override var songMenuRes: Int
			get() = R.menu.menu_item_cannot_delete_single_songs_playlist_song
			set(value) {
				super.songMenuRes = value
			}

		override fun onSongMenuItemClick(item: MenuItem): Boolean {
			if (item.itemId == R.id.action_go_to_album) {
				val activityOptions = ActivityOptions.makeSceneTransitionAnimation(
						activity, image, activity.getString(
						R.string.transition_album_art
				)
				)
				NavigationUtil.goToAlbumOptions(
						activity, dataSet[adapterPosition - 1].albumId, activityOptions
				)
				return true
			}
			return super.onSongMenuItemClick(item)
		}
	}

	companion object {
		val TAG: String = PlaylistSongAdapter::class.java.simpleName
	}
}