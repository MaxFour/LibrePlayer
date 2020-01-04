package com.maxfour.libreplayer.adapter

import android.app.ActivityOptions
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.maxfour.appthemehelper.ThemeStore
import com.maxfour.libreplayer.R
import com.maxfour.libreplayer.adapter.base.MediaEntryViewHolder
import com.maxfour.libreplayer.glide.ArtistGlideRequest
import com.maxfour.libreplayer.glide.SongGlideRequest
import com.maxfour.libreplayer.helper.MusicPlayerRemote
import com.maxfour.libreplayer.helper.menu.SongMenuHelper
import com.maxfour.libreplayer.model.Album
import com.maxfour.libreplayer.model.Artist
import com.maxfour.libreplayer.model.Genre
import com.maxfour.libreplayer.model.Song
import com.maxfour.libreplayer.util.MusicUtil
import com.maxfour.libreplayer.util.NavigationUtil
import android.util.Pair as UtilPair

class SearchAdapter(
		private val activity: AppCompatActivity, private var dataSet: List<Any>?
) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

	fun swapDataSet(dataSet: MutableList<Any>) {
		this.dataSet = dataSet
		notifyDataSetChanged()
	}

	override fun getItemViewType(position: Int): Int {
		if (dataSet!![position] is Album) return ALBUM
		if (dataSet!![position] is Artist) return ARTIST
		if (dataSet!![position] is Genre) return GENRE
		return if (dataSet!![position] is Song) SONG else HEADER
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		return if (viewType == HEADER) ViewHolder(LayoutInflater.from(activity).inflate(R.layout.sub_header, parent, false), viewType)
		else
			ViewHolder(LayoutInflater.from(activity).inflate(R.layout.item_list, parent, false), viewType)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		when (getItemViewType(position)) {
			ALBUM -> {
				val album = dataSet?.get(position) as Album
				holder.title?.text = album.title
				holder.text?.text = album.artistName
				SongGlideRequest.Builder.from(Glide.with(activity), album.safeGetFirstSong())
						.checkIgnoreMediaStore(activity).build().into(holder.image)
			}
			ARTIST -> {
				val artist = dataSet?.get(position) as Artist
				holder.title?.text = artist.name
				holder.text?.text = MusicUtil.getArtistInfoString(activity, artist)
				ArtistGlideRequest.Builder.from(Glide.with(activity), artist).build()
						.into(holder.image)
			}
			SONG -> {
				val song = dataSet?.get(position) as Song
				holder.title?.text = song.title
				holder.text?.text = song.albumName
			}
			GENRE -> {
				val genre = dataSet?.get(position) as Genre
				holder.title?.text = genre.name
			}
			else -> {
				holder.title?.text = dataSet?.get(position).toString()
				holder.title?.setTextColor(ThemeStore.accentColor(activity))
			}
		}
	}

	override fun getItemCount(): Int {
		return dataSet!!.size
	}

	inner class ViewHolder(itemView: View, itemViewType: Int) : MediaEntryViewHolder(itemView) {
		init {
			itemView.setOnLongClickListener(null)

			if (itemViewType == SONG) {
				menu?.visibility = View.VISIBLE
				menu?.setOnClickListener(object : SongMenuHelper.OnClickSongMenu(activity) {
					override val song: Song
						get() = dataSet!![adapterPosition] as Song
				})
			} else {
				menu?.visibility = View.GONE
			}

			when (itemViewType) {
				ALBUM  -> setImageTransitionName(activity.getString(R.string.transition_album_art))
				ARTIST -> setImageTransitionName(activity.getString(R.string.transition_artist_image))
				else   -> {
					val container = itemView.findViewById<View>(R.id.imageContainer)
					container?.visibility = View.GONE
				}
			}
		}

		override fun onClick(v: View?) {
			val item = dataSet!![adapterPosition]
			when (itemViewType) {
				ALBUM -> {
					val options = ActivityOptions.makeSceneTransitionAnimation(activity, UtilPair.create(image, activity.getString(R.string.transition_album_art)))
					NavigationUtil.goToAlbumOptions(activity, (item as Album).id, options)
				}
				ARTIST -> {
					val options = ActivityOptions.makeSceneTransitionAnimation(activity, UtilPair.create(image, activity.getString(R.string.transition_artist_image)))
					NavigationUtil.goToArtistOptions(activity, (item as Artist).id, options)
				}
				GENRE -> {
					NavigationUtil.goToGenre(activity, item as Genre)
				}
				SONG -> {
					val playList = ArrayList<Song>()
					playList.add(item as Song)
					MusicPlayerRemote.openQueue(playList, 0, true)
				}
			}
		}
	}

	companion object {
		private const val HEADER = 0
		private const val ALBUM = 1
		private const val ARTIST = 2
		private const val SONG = 3
		private const val GENRE = 4
	}
}
