package com.maxfour.music.mvp.presenter

import com.maxfour.music.Result
import com.maxfour.music.model.Album
import com.maxfour.music.mvp.BaseView
import com.maxfour.music.mvp.Presenter
import com.maxfour.music.mvp.PresenterImpl
import com.maxfour.music.providers.interfaces.Repository
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

interface AlbumsView : BaseView {
    fun albums(albums: ArrayList<Album>)
}

interface AlbumsPresenter : Presenter<AlbumsView> {

    fun loadAlbums()

    class AlbumsPresenterImpl @Inject constructor(
            private val repository: Repository
    ) : PresenterImpl<AlbumsView>(), AlbumsPresenter, CoroutineScope {
        private val job = Job()

        override val coroutineContext: CoroutineContext
            get() = Dispatchers.IO + job

        override fun detachView() {
            super.detachView()
            job.cancel()
        }

        override fun loadAlbums() {
            launch {
                when (val result = repository.allAlbums()) {
                    is Result.Success -> {
                        withContext(Dispatchers.Main) {
                            view?.albums(result.data)
                        }
                    }
                    is Result.Error -> {
                        view?.showEmptyView()
                    }
                }
            }
        }
    }
}