package com.maxfour.music.fragments.mainactivity

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.maxfour.music.App
import com.maxfour.music.R
import com.maxfour.music.adapter.playlist.PlaylistAdapter
import com.maxfour.music.fragments.base.AbsLibraryPagerRecyclerViewFragment
import com.maxfour.music.model.Playlist
import com.maxfour.music.mvp.presenter.PlaylistView
import com.maxfour.music.mvp.presenter.PlaylistsPresenter
import javax.inject.Inject


class PlaylistsFragment : AbsLibraryPagerRecyclerViewFragment<PlaylistAdapter, LinearLayoutManager>(), PlaylistView {

    @Inject
    lateinit var playlistsPresenter: PlaylistsPresenter

    override val emptyMessage: Int
        get() = R.string.no_playlists

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.musicComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playlistsPresenter.attachView(this)
    }

    override fun createLayoutManager(): LinearLayoutManager {
        return LinearLayoutManager(activity)
    }

    override fun createAdapter(): PlaylistAdapter {
        return PlaylistAdapter(libraryFragment.mainActivity, ArrayList(),
                R.layout.item_list, libraryFragment)
    }

    override fun onResume() {
        super.onResume()
        if (adapter!!.dataSet.isEmpty()) {
            playlistsPresenter.playlists()
        }
    }

    override fun onDestroyView() {
        playlistsPresenter.detachView()
        super.onDestroyView()
    }

    override fun onMediaStoreChanged() {
        super.onMediaStoreChanged()
        playlistsPresenter.playlists()
    }

    override fun showEmptyView() {
        adapter?.swapDataSet(ArrayList())
    }

    override fun playlists(playlists: ArrayList<Playlist>) {
        adapter?.swapDataSet(playlists)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.apply {
            removeItem(R.id.action_sort_order)
            removeItem(R.id.action_grid_size)
        }
    }

    companion object {
        @JvmField
        val TAG: String = PlaylistsFragment::class.java.simpleName

        fun newInstance(): PlaylistsFragment {
            val args = Bundle()
            val fragment = PlaylistsFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
