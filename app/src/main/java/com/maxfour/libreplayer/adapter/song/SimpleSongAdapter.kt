package com.maxfour.libreplayer.adapter.song

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.maxfour.libreplayer.interfaces.CabHolder
import com.maxfour.libreplayer.model.Song
import com.maxfour.libreplayer.util.MusicUtil
import java.util.*

class SimpleSongAdapter(
		context: AppCompatActivity,
		songs: ArrayList<Song>,
		i: Int,
		cabHolder: CabHolder?
) : SongAdapter(context, songs, i, false, cabHolder) {

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
		val fixedSongNumber = MusicUtil.getFixedSongNumber(dataSet[position].songNumber)

		holder.imageText?.text = if (fixedSongNumber > 0) fixedSongNumber.toString() else "-"
		holder.time?.text = MusicUtil.getReadableDurationString(dataSet[position].duration)
	}

	override fun getItemCount(): Int {
		return dataSet.size
	}
}
