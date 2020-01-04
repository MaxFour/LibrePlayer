package com.maxfour.libreplayer.fragments.mainactivity

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.maxfour.libreplayer.App
import com.maxfour.libreplayer.R
import com.maxfour.libreplayer.adapter.GenreAdapter
import com.maxfour.libreplayer.fragments.base.AbsLibraryPagerRecyclerViewFragment
import com.maxfour.libreplayer.model.Genre
import com.maxfour.libreplayer.mvp.presenter.GenresPresenter
import com.maxfour.libreplayer.mvp.presenter.GenresView
import javax.inject.Inject

class GenresFragment : AbsLibraryPagerRecyclerViewFragment<GenreAdapter, LinearLayoutManager>(), GenresView {
    override fun genres(genres: ArrayList<Genre>) {
        adapter?.swapDataSet(genres)
    }

    override fun showEmptyView() {

    }

    override fun createLayoutManager(): LinearLayoutManager {
        return LinearLayoutManager(activity)
    }

    override fun createAdapter(): GenreAdapter {
        val dataSet = if (adapter == null) ArrayList() else adapter!!.dataSet
        return GenreAdapter(libraryFragment.mainActivity, dataSet, R.layout.item_list_no_image)
    }

    override val emptyMessage: Int
        get() = R.string.no_genres


    @Inject
    lateinit var genresPresenter: GenresPresenter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.musicComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        genresPresenter.attachView(this)
    }
    override fun onResume() {
        super.onResume()
        if (adapter!!.dataSet.isEmpty()) {
            genresPresenter.loadGenres()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        genresPresenter.detachView()
    }

    override fun onMediaStoreChanged() {
        genresPresenter.loadGenres()
    }

    companion object {
        @JvmField
        val TAG: String = GenresFragment::class.java.simpleName

        fun newInstance(): GenresFragment {
            return GenresFragment()
        }
    }
}
