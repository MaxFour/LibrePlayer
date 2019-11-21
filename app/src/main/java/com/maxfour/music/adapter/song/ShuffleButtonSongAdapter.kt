package com.maxfour.music.adapter.song

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.maxfour.music.R
import com.maxfour.music.helper.MusicPlayerRemote
import com.maxfour.music.interfaces.CabHolder
import com.maxfour.music.model.Song
import java.util.*

class ShuffleButtonSongAdapter(
		activity: AppCompatActivity,
		dataSet: ArrayList<Song>,
		itemLayoutRes: Int,
		usePalette: Boolean,
		cabHolder: CabHolder?
) : AbsOffsetSongAdapter(activity, dataSet, itemLayoutRes, usePalette, cabHolder) {

	override fun createViewHolder(view: View): SongAdapter.ViewHolder {
		return ViewHolder(view)
	}

	override fun onBindViewHolder(holder: SongAdapter.ViewHolder, position: Int) {
		if (holder.itemViewType == OFFSET_ITEM) {
			val viewHolder = holder as ViewHolder
			viewHolder.playAction?.let {
				it.setOnClickListener {
					MusicPlayerRemote.openQueue(dataSet, 0, true)
				}
			}
			viewHolder.shuffleAction?.let {
				it.setOnClickListener {
					MusicPlayerRemote.openAndShuffleQueue(dataSet, true)
				}
			}
		} else {
			super.onBindViewHolder(holder, position - 1)
		}
	}

	inner class ViewHolder(itemView: View) : AbsOffsetSongAdapter.ViewHolder(itemView) {
		val playAction: MaterialButton? = itemView.findViewById(R.id.playAction)
		val shuffleAction: MaterialButton? = itemView.findViewById(R.id.shuffleAction)

		override fun onClick(v: View?) {
			if (itemViewType == OFFSET_ITEM) {
				MusicPlayerRemote.openAndShuffleQueue(dataSet, true)
				return
			}
			super.onClick(v)
		}
	}
}