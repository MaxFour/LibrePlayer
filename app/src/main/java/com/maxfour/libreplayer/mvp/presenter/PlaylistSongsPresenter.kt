package com.maxfour.libreplayer.mvp.presenter

import com.maxfour.libreplayer.Result
import com.maxfour.libreplayer.model.Playlist
import com.maxfour.libreplayer.model.Song
import com.maxfour.libreplayer.mvp.BaseView
import com.maxfour.libreplayer.mvp.Presenter
import com.maxfour.libreplayer.mvp.PresenterImpl
import com.maxfour.libreplayer.providers.interfaces.Repository
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

interface PlaylistSongsView : BaseView {
    fun songs(songs: ArrayList<Song>)
}

interface PlaylistSongsPresenter : Presenter<PlaylistSongsView> {
    fun loadPlaylistSongs(playlist: Playlist)

    class PlaylistSongsPresenterImpl @Inject constructor(
            private val repository: Repository
    ) : PresenterImpl<PlaylistSongsView>(), PlaylistSongsPresenter, CoroutineScope {

        private var job: Job = Job()

        override val coroutineContext: CoroutineContext
            get() = Dispatchers.IO + job

        override fun loadPlaylistSongs(playlist: Playlist) {
            launch {
                when (val songs = repository.getPlaylistSongs(playlist)) {
                    is Result.Success -> withContext(Dispatchers.Main) {
                        view?.songs(songs.data)
                    }
                    is Result.Error -> withContext(Dispatchers.Main) { view?.showEmptyView() }
                }
            }
        }

        override fun detachView() {
            super.detachView()
            job.cancel()
        }
    }
}
