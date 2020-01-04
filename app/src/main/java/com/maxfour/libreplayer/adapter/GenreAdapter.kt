package com.maxfour.libreplayer.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.maxfour.libreplayer.R
import com.maxfour.libreplayer.adapter.base.MediaEntryViewHolder
import com.maxfour.libreplayer.model.Genre
import com.maxfour.libreplayer.util.NavigationUtil
import java.util.*

class GenreAdapter(
		private val activity: Activity, dataSet: ArrayList<Genre>, private val mItemLayoutRes: Int
) : RecyclerView.Adapter<GenreAdapter.ViewHolder>() {
	var dataSet = ArrayList<Genre>()
		private set

	init {
		this.dataSet = dataSet
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		return ViewHolder(LayoutInflater.from(activity).inflate(mItemLayoutRes, parent, false))
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val genre = dataSet[position]
		holder.title?.text = genre.name
		holder.text?.text = String.format(Locale.getDefault(), "%d %s", genre.songCount, if (genre.songCount > 1) activity.getString(R.string.songs) else activity.getString(R.string.song))
	}

	override fun getItemCount(): Int {
		return dataSet.size
	}

	fun swapDataSet(list: ArrayList<Genre>) {
		dataSet = list
		notifyDataSetChanged()
	}

	inner class ViewHolder(itemView: View) : MediaEntryViewHolder(itemView) {
		override fun onClick(v: View?) {
			super.onClick(v)
			val genre = dataSet[adapterPosition]
			NavigationUtil.goToGenre(activity, genre)
		}
	}
}
