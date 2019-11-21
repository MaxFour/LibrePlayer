package com.maxfour.music.adapter.song

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.maxfour.music.model.Song
import com.maxfour.music.util.MusicUtil
import java.util.*

class SimpleSongAdapter(
		context: AppCompatActivity, songs: ArrayList<Song>, i: Int
) : SongAdapter(context, songs, i, false, null) {

	override fun swapDataSet(dataSet: ArrayList<Song>) {
		this.dataSet.clear()
		this.dataSet = dataSet
		notifyDataSetChanged()
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		return ViewHolder(LayoutInflater.from(activity).inflate(itemLayoutRes, parent, false))
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		super.onBindViewHolder(holder, position)
		val fixedTrackNumber = MusicUtil.getFixedTrackNumber(dataSet[position].trackNumber)

		holder.imageText?.text = if (fixedTrackNumber > 0) fixedTrackNumber.toString() else "-"
		holder.time?.text = MusicUtil.getReadableDurationString(dataSet[position].duration)
	}

	override fun getItemCount(): Int {
		return dataSet.size
	}
}
