package com.maxfour.libreplayer.adapter.song

import android.graphics.Color
import android.graphics.PorterDuff.Mode
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange
import com.h6ah4i.android.widget.advrecyclerview.draggable.annotation.DraggableItemStateFlags
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionDefault
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionRemoveItem
import com.h6ah4i.android.widget.advrecyclerview.swipeable.annotation.SwipeableItemResults
import com.maxfour.libreplayer.R
import com.maxfour.libreplayer.helper.MusicPlayerRemote
import com.maxfour.libreplayer.helper.MusicPlayerRemote.isPlaying
import com.maxfour.libreplayer.helper.MusicPlayerRemote.playNextSong
import com.maxfour.libreplayer.helper.MusicPlayerRemote.removeFromQueue
import com.maxfour.libreplayer.model.Song
import com.maxfour.libreplayer.util.MusicUtil
import com.maxfour.libreplayer.util.ViewUtil
import java.util.ArrayList

class PlayingQueueAdapter(
	activity: AppCompatActivity,
	dataSet: ArrayList<Song>,
	private var current: Int,
	itemLayoutRes: Int
) : SongAdapter(
	activity, dataSet, itemLayoutRes, false, null
), DraggableItemAdapter<PlayingQueueAdapter.ViewHolder>, SwipeableItemAdapter<PlayingQueueAdapter.ViewHolder> {

	private var color = -1
	private var songToRemove: Song? = null

	override fun createViewHolder(view: View): SongAdapter.ViewHolder {
		return ViewHolder(view)
	}

	override fun onBindViewHolder(holder: SongAdapter.ViewHolder, position: Int) {
		super.onBindViewHolder(holder, position)
		holder.imageText?.text = (position - current).toString()
		holder.time?.text = MusicUtil.getReadableDurationString(dataSet[position].duration)
		if (holder.itemViewType == HISTORY || holder.itemViewType == CURRENT) {
			setAlpha(holder, 0.5f)
		}
		if (usePalette) {
			setColor(holder, Color.WHITE)
		}
	}

	private fun setColor(holder: SongAdapter.ViewHolder, white: Int) {

		if (holder.title != null) {
			holder.title!!.setTextColor(white)
			if (color != -1) {
				holder.title!!.setTextColor(color)
			}
		}

		holder.text?.setTextColor(white)
		holder.time?.setTextColor(white)
		holder.imageText?.setTextColor(white)
		if (holder.menu != null) {
			(holder.menu as ImageView).setColorFilter(white, Mode.SRC_IN)
		}
	}

	override fun usePalette(usePalette: Boolean) {
		super.usePalette(usePalette)
		this.usePalette = usePalette
		notifyDataSetChanged()
	}

	override fun getItemViewType(position: Int): Int {
		if (position < current) {
			return HISTORY
		} else if (position > current) {
			return UP_NEXT
		}
		return CURRENT
	}

	override fun loadAlbumCover(song: Song, holder: SongAdapter.ViewHolder) {
		// We don't want to load it in this adapter
	}

	fun swapDataSet(dataSet: ArrayList<Song>, position: Int) {
		this.dataSet = dataSet
		current = position
		notifyDataSetChanged()
	}

	fun setCurrent(current: Int) {
		this.current = current
		notifyDataSetChanged()
	}

	private fun setAlpha(holder: SongAdapter.ViewHolder, alpha: Float) {
		holder.image?.alpha = alpha
		holder.title?.alpha = alpha
		holder.text?.alpha = alpha
		holder.imageText?.alpha = alpha
		holder.paletteColorContainer?.alpha = alpha
		holder.dragView?.alpha = alpha
		holder.menu?.alpha = alpha
	}

	override fun onCheckCanStartDrag(holder: ViewHolder, position: Int, x: Int, y: Int): Boolean {
		return ViewUtil.hitTest(holder.imageText!!, x, y) || ViewUtil.hitTest(holder.dragView!!, x, y)
	}

	override fun onGetItemDraggableRange(holder: ViewHolder, position: Int): ItemDraggableRange? {
		return null
	}

	override fun onMoveItem(fromPosition: Int, toPosition: Int) {
		MusicPlayerRemote.moveSong(fromPosition, toPosition)
	}

	override fun onCheckCanDrop(draggingPosition: Int, dropPosition: Int): Boolean {
		return true
	}

	override fun onItemDragStarted(position: Int) {
		notifyDataSetChanged()
	}

	override fun onItemDragFinished(fromPosition: Int, toPosition: Int, result: Boolean) {
		notifyDataSetChanged()
	}

	fun setSongToRemove(song: Song) {
		songToRemove = song
	}

	inner class ViewHolder(itemView: View) : SongAdapter.ViewHolder(itemView) {

		@DraggableItemStateFlags
		private var mDragStateFlags: Int = 0

		override var songMenuRes: Int
			get() = R.menu.menu_item_playing_queue_song
			set(value) {
				super.songMenuRes = value
			}

		init {
			imageText?.visibility = View.VISIBLE
			dragView?.visibility = View.VISIBLE
		}

		override fun onSongMenuItemClick(item: MenuItem): Boolean {
			when (item.itemId) {
				R.id.action_remove_from_playing_queue -> {
					removeFromQueue(adapterPosition)
					return true
				}
			}
			return super.onSongMenuItemClick(item)
		}

		@DraggableItemStateFlags
		override fun getDragStateFlags(): Int {
			return mDragStateFlags
		}

		override fun setDragStateFlags(@DraggableItemStateFlags flags: Int) {
			mDragStateFlags = flags
		}

		override fun getSwipeableContainerView(): View? {
			return dummyContainer
		}
	}

	companion object {

		private const val HISTORY = 0
		private const val CURRENT = 1
		private const val UP_NEXT = 2
	}

	override fun onSwipeItem(
			holder: ViewHolder?,
			position: Int, @SwipeableItemResults result: Int
	): SwipeResultAction {
		return if (result === SwipeableItemConstants.RESULT_CANCELED) {
			SwipeResultActionDefault()
		} else {
			SwipedResultActionRemoveItem(this, position, activity)
		}
	}

	override fun onGetSwipeReactionType(holder: ViewHolder?, position: Int, x: Int, y: Int): Int {
		return if (onCheckCanStartDrag(holder!!, position, x, y)) {
			SwipeableItemConstants.REACTION_CAN_NOT_SWIPE_BOTH_H;
		} else {
			SwipeableItemConstants.REACTION_CAN_SWIPE_BOTH_H;
		}
	}

	override fun onSwipeItemStarted(p0: ViewHolder?, p1: Int) {
	}

	override fun onSetSwipeBackground(holder: ViewHolder?, position: Int, result: Int) {
	}

	internal class SwipedResultActionRemoveItem(
			private val adapter: PlayingQueueAdapter,
			private val position: Int,
			private val activity: AppCompatActivity
	) : SwipeResultActionRemoveItem() {

		private var songToRemove: Song? = null
		private val isPlaying: Boolean = MusicPlayerRemote.isPlaying
		private val songProgressMillis = 0
		override fun onPerformAction() {
			//currentlyShownSnackbar = null
		}

		override fun onSlideAnimationEnd() {
			//initializeSnackBar(adapter, position, activity, isPlaying)
			songToRemove = adapter.dataSet[position]
			//If song removed was the playing song, then play the next song
			if (isPlaying(songToRemove!!)) {
				playNextSong()
			}
			//Swipe animation is much smoother when we do the heavy lifting after it's completed
			adapter.setSongToRemove(songToRemove!!)
			removeFromQueue(songToRemove!!)
		}
	}
}
