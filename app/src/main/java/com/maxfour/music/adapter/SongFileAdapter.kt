package com.maxfour.music.adapter

import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.MediaStoreSignature
import com.maxfour.appthemehelper.util.ATHUtil
import com.maxfour.music.R
import com.maxfour.music.adapter.base.AbsMultiSelectAdapter
import com.maxfour.music.adapter.base.MediaEntryViewHolder
import com.maxfour.music.glide.audiocover.AudioFileCover
import com.maxfour.music.interfaces.CabHolder
import com.maxfour.music.util.MusicPlayerUtil
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import java.io.File
import java.text.DecimalFormat
import java.util.*
import kotlin.math.log10
import kotlin.math.pow

class SongFileAdapter(
		private val activity: AppCompatActivity,
		private var dataSet: List<File>?,
		private val itemLayoutRes: Int,
		private val callbacks: Callbacks?,
		cabHolder: CabHolder?
) : AbsMultiSelectAdapter<SongFileAdapter.ViewHolder, File>(
		activity, cabHolder, R.menu.menu_media_selection
), FastScrollRecyclerView.SectionedAdapter {

	init {
		this.setHasStableIds(true)
	}

	override fun getItemViewType(position: Int): Int {
		return if (dataSet!![position].isDirectory) FOLDER else FILE
	}

	override fun getItemId(position: Int): Long {
		return dataSet!![position].hashCode().toLong()
	}

	fun swapDataSet(songFiles: List<File>) {
		this.dataSet = songFiles
		notifyDataSetChanged()
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		return ViewHolder(LayoutInflater.from(activity).inflate(itemLayoutRes, parent, false))
	}

	override fun onBindViewHolder(holder: ViewHolder, index: Int) {
		val file = dataSet!![index]
		holder.itemView.isActivated = isChecked(file)
		holder.title?.text = getFileTitle(file)
		if (holder.text != null) {
			if (holder.itemViewType == FILE) {
				holder.text?.text = getFileText(file)
			} else {
				holder.text?.visibility = View.GONE
			}
		}

		if (holder.image != null) {
			loadFileImage(file, holder)
		}
	}

	private fun getFileTitle(file: File): String {
		return file.name
	}

	private fun getFileText(file: File): String? {
		return if (file.isDirectory) null else readableFileSize(file.length())
	}

	private fun loadFileImage(file: File, holder: ViewHolder) {
		val iconColor = ATHUtil.resolveColor(activity, R.attr.iconColor)
		if (file.isDirectory) {
			holder.image?.let {
				it.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN)
				it.setImageResource(R.drawable.ic_folder_white_24dp)
			}
			holder.imageTextContainer?.setCardBackgroundColor(
					ATHUtil.resolveColor(
							activity, R.attr.colorPrimary
					)
			)

		} else {
			val error = MusicPlayerUtil.getTintedVectorDrawable(
					activity, R.drawable.ic_file_music_white_24dp, iconColor
			)
			Glide.with(activity).load(AudioFileCover(file.path))
				.diskCacheStrategy(DiskCacheStrategy.NONE).error(error).placeholder(error)
				.animate(android.R.anim.fade_in)
				.signature(MediaStoreSignature("", file.lastModified(), 0)).into(holder.image)
		}
	}

	override fun getItemCount(): Int {
		return dataSet!!.size
	}

	override fun getIdentifier(position: Int): File? {
		return dataSet!![position]
	}

	override fun getName(`object`: File): String {
		return getFileTitle(`object`)
	}

	override fun onMultipleItemAction(menuItem: MenuItem, selection: ArrayList<File>) {
		if (callbacks == null) return
		callbacks.onMultipleItemAction(menuItem, selection)
	}

	override fun getSectionName(position: Int): String {
		return dataSet!![position].name[0].toString().toUpperCase()
	}

	interface Callbacks {
		fun onFileSelected(file: File)

		fun onFileMenuClicked(file: File, view: View)

		fun onMultipleItemAction(item: MenuItem, files: ArrayList<File>)
	}

	inner class ViewHolder(itemView: View) : MediaEntryViewHolder(itemView) {

		init {
			if (menu != null && callbacks != null) {
				menu!!.setOnClickListener { v ->
					val position = adapterPosition
					if (isPositionInRange(position)) {
						callbacks.onFileMenuClicked(dataSet!![position], v)
					}
				}
			}
			if (imageTextContainer != null) {
				imageTextContainer?.cardElevation = 0f
			}
		}

		override fun onClick(v: View?) {
			val position = adapterPosition
			if (isPositionInRange(position)) {
				if (isInQuickSelectMode) {
					toggleChecked(position)
				} else {
					callbacks?.onFileSelected(dataSet!![position])
				}
			}
		}

		override fun onLongClick(v: View?): Boolean {
			val position = adapterPosition
			return isPositionInRange(position) && toggleChecked(position)
		}

		private fun isPositionInRange(position: Int): Boolean {
			return position >= 0 && position < dataSet!!.size
		}
	}

	companion object {

		private const val FILE = 0
		private const val FOLDER = 1

		fun readableFileSize(size: Long): String {
			if (size <= 0) return "$size B"
			val units = arrayOf("B", "KB", "MB", "GB", "TB")
			val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()
			return DecimalFormat("#,##0.##").format(size / 1024.0.pow(digitGroups.toDouble())) + " " + units[digitGroups]
		}
	}
}