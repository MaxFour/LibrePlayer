package com.maxfour.music.mvp.presenter

import com.maxfour.music.Result
import com.maxfour.music.model.Playlist
import com.maxfour.music.mvp.BaseView
import com.maxfour.music.mvp.Presenter
import com.maxfour.music.mvp.PresenterImpl
import com.maxfour.music.providers.interfaces.Repository
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
                    is Result.Error -> withContext(Dispatchers.Main) {
                        view?.showEmptyView()
                    }
                }
            }
        }
    }
}


