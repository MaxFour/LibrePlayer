package com.maxfour.music.adapter.playlist

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import com.maxfour.appthemehelper.ThemeStore
import com.maxfour.appthemehelper.util.ATHUtil
import com.maxfour.appthemehelper.util.TintHelper
import com.maxfour.music.R
import com.maxfour.music.adapter.base.AbsMultiSelectAdapter
import com.maxfour.music.adapter.base.MediaEntryViewHolder
import com.maxfour.music.dialogs.ClearSmartPlaylistDialog
import com.maxfour.music.dialogs.DeletePlaylistDialog
import com.maxfour.music.helper.menu.PlaylistMenuHelper
import com.maxfour.music.helper.menu.SongsMenuHelper
import com.maxfour.music.interfaces.CabHolder
import com.maxfour.music.loaders.PlaylistSongsLoader
import com.maxfour.music.model.AbsCustomPlaylist
import com.maxfour.music.model.Playlist
import com.maxfour.music.model.Song
import com.maxfour.music.model.smartplaylist.AbsSmartPlaylist
import com.maxfour.music.model.smartplaylist.LastAddedPlaylist
import com.maxfour.music.util.MusicUtil
import com.maxfour.music.util.NavigationUtil
import java.util.*

class PlaylistAdapter(
		private val activity: AppCompatActivity,
		var dataSet: ArrayList<Playlist>,
		private var itemLayoutRes: Int,
		cabHolder: CabHolder?
) : AbsMultiSelectAdapter<PlaylistAdapter.ViewHolder, Playlist>(
		activity,
		cabHolder,
		R.menu.menu_playlists_selection
) {

	var songs = ArrayList<Song>()

	init {
		setHasStableIds(true)
	}

	fun swapDataSet(dataSet: ArrayList<Playlist>) {
		this.dataSet = dataSet
		notifyDataSetChanged()
	}

	override fun getItemId(position: Int): Long {
		return dataSet[position].id.toLong()
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val view = LayoutInflater.from(activity).inflate(itemLayoutRes, parent, false)
		return createViewHolder(view)
	}

	protected fun createViewHolder(view: View): ViewHolder {
		return ViewHolder(view)
	}

	protected fun getPlaylistTitle(playlist: Playlist): String {
		return playlist.name
	}

	protected fun getPlaylistText(playlist: Playlist): String {
		return MusicUtil.getPlaylistInfoString(activity, getSongs(playlist))
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val playlist = dataSet[position]
		holder.itemView.isActivated = isChecked(playlist)
		holder.title?.text = getPlaylistTitle(playlist)
		holder.text?.text = getPlaylistText(playlist)
		holder.image?.setImageDrawable(getIconRes(playlist))
	}

	private fun getIconRes(playlist: Playlist): Drawable {
		if (playlist is AbsSmartPlaylist) {
			return TintHelper.createTintedDrawable(
					activity,
					playlist.iconRes,
					ATHUtil.resolveColor(activity, R.attr.iconColor)
			)!!
		}
		return if (MusicUtil.isFavoritePlaylist(
						activity,
						playlist
				)) TintHelper.createTintedDrawable(
				activity,
				R.drawable.ic_favorite_white_24dp,
				ThemeStore.accentColor(activity)
		)!!
		else TintHelper.createTintedDrawable(
				activity,
				R.drawable.ic_playlist_play_white_24dp,
				ATHUtil.resolveColor(activity, R.attr.iconColor)
		)!!
	}

	override fun getItemViewType(position: Int): Int {
		return if (dataSet[position] is AbsSmartPlaylist) SMART_PLAYLIST else DEFAULT_PLAYLIST
	}

	override fun getItemCount(): Int {
		return dataSet.size
	}

	override fun getIdentifier(position: Int): Playlist? {
		return dataSet[position]
	}

	override fun getName(playlist: Playlist): String {
		return playlist.name
	}

	override fun onMultipleItemAction(menuItem: MenuItem, selection: ArrayList<Playlist>) {
		when (menuItem.itemId) {
			R.id.action_delete_playlist -> {
				var i = 0
				while (i < selection.size) {
					val playlist = selection[i]
					if (playlist is AbsSmartPlaylist) {
						ClearSmartPlaylistDialog.create(playlist).show(
								activity.supportFragmentManager, "CLEAR_PLAYLIST_" + playlist.name
						)
						selection.remove(playlist)
						i--
					}
					i++
				}
				if (selection.size > 0) {
					DeletePlaylistDialog.create(selection)
						.show(activity.supportFragmentManager, "DELETE_PLAYLIST")
				}
			}
			else                        -> SongsMenuHelper.handleMenuClick(
					activity,
					getSongList(selection),
					menuItem.itemId
			)
		}
	}

	private fun getSongList(playlists: List<Playlist>): ArrayList<Song> {
		val songs = ArrayList<Song>()
		for (playlist in playlists) {
			if (playlist is AbsCustomPlaylist) {
				songs.addAll(playlist.getSongs(activity))
			} else {
				songs.addAll(PlaylistSongsLoader.getPlaylistSongList(activity, playlist.id))
			}
		}
		return songs
	}

	private fun getSongs(playlist: Playlist): ArrayList<Song> {
		val songs = ArrayList<Song>()
		if (playlist is AbsSmartPlaylist) {
			songs.addAll(playlist.getSongs(activity))
		} else {
			songs.addAll(PlaylistSongsLoader.getPlaylistSongList(activity, playlist.id))
		}
		return songs
	}

	inner class ViewHolder(itemView: View) : MediaEntryViewHolder(itemView) {
		init {

			image?.apply {
				val iconPadding = activity.resources.getDimensionPixelSize(R.dimen.list_item_image_icon_padding)
				setPadding(iconPadding, iconPadding, iconPadding, iconPadding)
				//setColorFilter(ATHUtil.resolveColor(activity, R.attr.iconColor), PorterDuff.Mode.SRC_IN)
			}

			menu?.setOnClickListener { view ->
				val playlist = dataSet[adapterPosition]
				val popupMenu = PopupMenu(activity, view)
				popupMenu.inflate(
						if (itemViewType == SMART_PLAYLIST) R.menu.menu_item_smart_playlist
						else R.menu.menu_item_playlist
				)
				if (playlist is LastAddedPlaylist) {
					popupMenu.menu.findItem(R.id.action_clear_playlist).isVisible = false
				}
				popupMenu.setOnMenuItemClickListener { item ->
					if (item.itemId == R.id.action_clear_playlist) {
						if (playlist is AbsSmartPlaylist) {
							ClearSmartPlaylistDialog.create(playlist).show(
									activity.supportFragmentManager,
									"CLEAR_SMART_PLAYLIST_" + playlist.name
							)
							return@setOnMenuItemClickListener true
						}
					}
					PlaylistMenuHelper.handleMenuClick(
							activity, dataSet[adapterPosition], item
					)
				}
				popupMenu.show()
			}

			imageTextContainer?.apply {
				cardElevation = 0f
				setCardBackgroundColor(ATHUtil.resolveColor(activity, R.attr.colorPrimary))
			}
		}

		override fun onClick(v: View?) {
			if (isInQuickSelectMode) {
				toggleChecked(adapterPosition)
			} else {
				val playlist = dataSet[adapterPosition]
				NavigationUtil.goToPlaylistNew(activity, playlist)
			}
		}

		override fun onLongClick(v: View?): Boolean {
			toggleChecked(adapterPosition)
			return true
		}
	}

	companion object {
		val TAG: String = PlaylistAdapter::class.java.simpleName
		private const val SMART_PLAYLIST = 0
		private const val DEFAULT_PLAYLIST = 1
	}
}
