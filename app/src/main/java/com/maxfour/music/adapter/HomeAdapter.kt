package com.maxfour.music.adapter

import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IntDef
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.maxfour.music.R
import com.maxfour.music.adapter.album.AlbumFullWidthAdapter
import com.maxfour.music.adapter.artist.ArtistAdapter
import com.maxfour.music.adapter.song.SongAdapter
import com.maxfour.music.extensions.show
import com.maxfour.music.loaders.PlaylistSongsLoader
import com.maxfour.music.model.Album
import com.maxfour.music.model.Artist
import com.maxfour.music.model.Home
import com.maxfour.music.model.Playlist
import com.maxfour.music.util.PreferenceUtil

class HomeAdapter(
		private val activity: AppCompatActivity, private val displayMetrics: DisplayMetrics
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

	private var list = ArrayList<Home>()

	override fun getItemViewType(position: Int): Int {
		return list[position].homeSection
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		val layout = LayoutInflater.from(activity)
			.inflate(R.layout.section_recycler_view, parent, false)
		return when (viewType) {
			RECENT_ARTISTS, TOP_ARTISTS -> ArtistViewHolder(layout)
			PLAYLISTS                   -> PlaylistViewHolder(layout)
			else                        -> {
				AlbumViewHolder(
						LayoutInflater.from(activity).inflate(
								R.layout.metal_section_recycler_view, parent, false
						)
				)
			}
		}
	}

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		println("ViewType ${getItemViewType(position)}")
		when (getItemViewType(position)) {
			RECENT_ALBUMS -> {
				val viewHolder = holder as AlbumViewHolder
				viewHolder.bindView(
						list[position].arrayList.toAlbums(),
						R.string.recent_albums,
						R.string.recent_added_albums
				)
			}
			TOP_ALBUMS -> {
				val viewHolder = holder as AlbumViewHolder
				viewHolder.bindView(
						list[position].arrayList.toAlbums(),
						R.string.top_albums,
						R.string.most_played_albums
				)
			}
			RECENT_ARTISTS -> {
				val viewHolder = holder as ArtistViewHolder
				viewHolder.bindView(
						list[position].arrayList.toArtists(),
						R.string.recent_artists,
						R.string.recent_added_artists
				)
			}
			TOP_ARTISTS -> {
				val viewHolder = holder as ArtistViewHolder

				viewHolder.bindView(
						list[position].arrayList.toArtists(),
						R.string.top_artists,
						R.string.most_played_artists
				)
			}
			PLAYLISTS -> {
				val viewHolder = holder as PlaylistViewHolder
				viewHolder.bindView(
						list[position].arrayList.toPlaylist(),
						R.string.favorites,
						R.string.favorites_songs
				)
			}
		}
	}

	override fun getItemCount(): Int {
		return list.size
	}

	fun swapData(sections: ArrayList<Home>) {
		list = sections
		notifyDataSetChanged()
	}

	companion object {

		@IntDef(RECENT_ALBUMS, TOP_ALBUMS, RECENT_ARTISTS, TOP_ARTISTS, PLAYLISTS)
		@Retention(AnnotationRetention.SOURCE)
		annotation class HomeSection

		const val RECENT_ALBUMS = 3
		const val TOP_ALBUMS = 1
		const val RECENT_ARTISTS = 2
		const val TOP_ARTISTS = 0
		const val PLAYLISTS = 4

	}

	private inner class AlbumViewHolder(view: View) : AbsHomeViewItem(view) {
		fun bindView(list: ArrayList<Album>, titleRes: Int, subtitleRes: Int) {
			if (list.isNotEmpty()) {
				recyclerView.apply {
					show()
					adapter = AlbumFullWidthAdapter(activity, list, displayMetrics)
				}
				titleContainer.show()
				title.text = activity.getString(titleRes)
				text.text = activity.getString(subtitleRes)
			}
		}


	}

	inner class ArtistViewHolder(view: View) : AbsHomeViewItem(view) {
		fun bindView(list: ArrayList<Artist>, titleRes: Int, subtitleRes: Int) {
			if (list.isNotEmpty()) {
				recyclerView.apply {
					show()
					layoutManager = GridLayoutManager(
							activity, 1, GridLayoutManager.HORIZONTAL, false
					)
					val artistAdapter = ArtistAdapter(
							activity,
							list,
							PreferenceUtil.getInstance(activity).getHomeGridStyle(activity),
							false,
							null
					)
					adapter = artistAdapter
				}
				titleContainer.show()
				title.text = activity.getString(titleRes)
				text.text = activity.getString(subtitleRes)
			}
		}
	}

	private inner class PlaylistViewHolder(view: View) : AbsHomeViewItem(view) {
		fun bindView(arrayList: ArrayList<Playlist>, titleRes: Int, subtitleRes: Int) {
			if (arrayList.isNotEmpty()) {
				val songs = PlaylistSongsLoader.getPlaylistSongList(activity, arrayList[0])
				if (songs.isNotEmpty()) {
					recyclerView.apply {
						show()
						val songAdapter = SongAdapter(
								activity, songs, R.layout.item_album_card, false, null
						)
						layoutManager = GridLayoutManager(
								activity, 1, GridLayoutManager.HORIZONTAL, false
						)
						adapter = songAdapter

					}
					titleContainer.show()
					title.text = activity.getString(titleRes)
					text.text = activity.getString(subtitleRes)
				}
			}
		}
	}

	open inner class AbsHomeViewItem(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val recyclerView: RecyclerView = itemView.findViewById(R.id.recyclerView)
		val titleContainer: View = itemView.findViewById(R.id.titleContainer)
		val title: AppCompatTextView = itemView.findViewById(R.id.title)
		val text: MaterialTextView = itemView.findViewById(R.id.text)
	}
}

private fun <E> ArrayList<E>.toAlbums(): ArrayList<Album> {
	val arrayList = ArrayList<Album>()
	for (x in this) {
		arrayList.add(x as Album)
	}
	return arrayList;
}

private fun <E> ArrayList<E>.toArtists(): ArrayList<Artist> {
	val arrayList = ArrayList<Artist>()
	for (x in this) {
		arrayList.add(x as Artist)
	}
	return arrayList;
}

private fun <E> ArrayList<E>.toPlaylist(): ArrayList<Playlist> {
	val arrayList = ArrayList<Playlist>()
	for (x in this) {
		arrayList.add(x as Playlist)
	}
	return arrayList;
}

