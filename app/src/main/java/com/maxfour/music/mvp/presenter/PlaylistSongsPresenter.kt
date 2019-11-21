package com.maxfour.music.mvp.presenter

import com.maxfour.music.Result
import com.maxfour.music.model.Playlist
import com.maxfour.music.model.Song
import com.maxfour.music.mvp.BaseView
import com.maxfour.music.mvp.Presenter
import com.maxfour.music.mvp.PresenterImpl
import com.maxfour.music.providers.interfaces.Repository
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
                    is Result.Error -> withContext(Dispatchers.Main) {
                        view?.showEmptyView()
                    }
                }
            }
        }

        override fun detachView() {
            super.detachView()
            job.cancel()
        }
    }
}