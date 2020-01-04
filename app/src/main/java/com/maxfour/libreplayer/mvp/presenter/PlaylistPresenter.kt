package com.maxfour.libreplayer.mvp.presenter

import com.maxfour.libreplayer.Result
import com.maxfour.libreplayer.model.Playlist
import com.maxfour.libreplayer.mvp.BaseView
import com.maxfour.libreplayer.mvp.Presenter
import com.maxfour.libreplayer.mvp.PresenterImpl
import com.maxfour.libreplayer.providers.interfaces.Repository
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

interface PlaylistView : BaseView {
    fun playlists(playlists: ArrayList<Playlist>)
}

interface PlaylistsPresenter : Presenter<PlaylistView> {

    fun playlists()

    class PlaylistsPresenterImpl @Inject constructor(
            private val repository: Repository
    ) : PresenterImpl<PlaylistView>(), PlaylistsPresenter, CoroutineScope {

        private val job = Job()

        override val coroutineContext: CoroutineContext
            get() = Dispatchers.IO + job

        override fun detachView() {
            super.detachView()
            job.cancel()
        }

        override fun playlists() {
            launch {
                when (val result = repository.allPlaylists()) {
                    is Result.Success -> withContext(Dispatchers.Main) {
                        view?.playlists(result.data)
                    }
                    is Result.Error -> withContext(Dispatchers.Main) { view?.showEmptyView() }
                }
            }
        }
    }
}
