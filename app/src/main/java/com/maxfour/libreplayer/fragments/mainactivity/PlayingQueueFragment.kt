package com.maxfour.libreplayer.fragments.mainactivity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.h6ah4i.android.widget.advrecyclerview.animator.DraggableItemAnimator
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager
import com.h6ah4i.android.widget.advrecyclerview.touchguard.RecyclerViewTouchActionGuardManager
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils
import com.maxfour.libreplayer.R
import com.maxfour.libreplayer.adapter.song.PlayingQueueAdapter
import com.maxfour.libreplayer.fragments.base.AbsLibraryPagerRecyclerViewFragment
import com.maxfour.libreplayer.helper.MusicPlayerRemote
import com.maxfour.libreplayer.util.ViewUtil
import kotlinx.android.synthetic.main.activity_playing_queue.*

class PlayingQueueFragment : AbsLibraryPagerRecyclerViewFragment<PlayingQueueAdapter, LinearLayoutManager>() {

    private lateinit var wrappedAdapter: RecyclerView.Adapter<*>
    private var recyclerViewDragDropManager: RecyclerViewDragDropManager? = null
    private var recyclerViewSwipeManager: RecyclerViewSwipeManager? = null
    private var recyclerViewTouchActionGuardManager: RecyclerViewTouchActionGuardManager? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        recyclerViewTouchActionGuardManager = RecyclerViewTouchActionGuardManager()
        recyclerViewDragDropManager = RecyclerViewDragDropManager()
        recyclerViewSwipeManager = RecyclerViewSwipeManager()

        val animator = DraggableItemAnimator()
        animator.supportsChangeAnimations = false
        wrappedAdapter = recyclerViewDragDropManager?.createWrappedAdapter(adapter!!) as RecyclerView.Adapter<*>
        wrappedAdapter = recyclerViewSwipeManager?.createWrappedAdapter(wrappedAdapter) as RecyclerView.Adapter<*>
        recyclerView().layoutManager = layoutManager
        recyclerView().adapter = wrappedAdapter
        recyclerView().itemAnimator = animator
        recyclerViewTouchActionGuardManager?.attachRecyclerView(recyclerView)
        recyclerViewDragDropManager?.attachRecyclerView(recyclerView)
        recyclerViewSwipeManager?.attachRecyclerView(recyclerView)

        layoutManager?.scrollToPositionWithOffset(MusicPlayerRemote.position + 1, 0)
        ViewUtil.setUpFastScrollRecyclerViewColor(requireContext(), recyclerView)
    }

    override fun createLayoutManager(): LinearLayoutManager {
        return LinearLayoutManager(requireContext())
    }

    override fun createAdapter(): PlayingQueueAdapter {
        return PlayingQueueAdapter(requireActivity() as AppCompatActivity, MusicPlayerRemote.playingQueue, MusicPlayerRemote.position, R.layout.item_queue)
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        updateQueue()
    }

    override fun onQueueChanged() {
        super.onQueueChanged()
        updateQueue()
    }

    override fun onPlayingMetaChanged() {
        updateQueuePosition()
    }

    private fun updateQueuePosition() {
        adapter?.setCurrent(MusicPlayerRemote.position)
        resetToCurrentPosition()
    }

    private fun updateQueue() {
        adapter?.swapDataSet(MusicPlayerRemote.playingQueue, MusicPlayerRemote.position)
        resetToCurrentPosition()
    }

    private fun resetToCurrentPosition() {
        recyclerView.stopScroll()
        layoutManager?.scrollToPositionWithOffset(MusicPlayerRemote.position + 1, 0)
    }

    override fun onPause() {
        recyclerViewDragDropManager?.cancelDrag()
        super.onPause()
    }

    override val emptyMessage: Int
        get() = R.string.no_playing_queue

    override fun onDestroyView() {
        super.onDestroyView()
        if (recyclerViewDragDropManager != null) {
            recyclerViewDragDropManager?.release()
            recyclerViewDragDropManager = null
        }

        if (recyclerViewSwipeManager != null) {
            recyclerViewSwipeManager?.release()
            recyclerViewSwipeManager = null
        }

        WrapperAdapterUtils.releaseAll(wrappedAdapter)
    }

    companion object {
        @JvmField
        val TAG: String = PlayingQueueFragment::class.java.simpleName

        fun newInstance(): PlayingQueueFragment {
            return PlayingQueueFragment()
        }
    }
}
