package com.maxfour.libreplayer.mvp.presenter

import com.maxfour.libreplayer.Result
import com.maxfour.libreplayer.model.Album
import com.maxfour.libreplayer.mvp.BaseView
import com.maxfour.libreplayer.mvp.Presenter
import com.maxfour.libreplayer.mvp.PresenterImpl
import com.maxfour.libreplayer.providers.interfaces.Repository
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
                    is Result.Success -> withContext(Dispatchers.Main) {
                        view?.albums(result.data)
                    }
                    is Result.Error   -> withContext(Dispatchers.Main) { view?.showEmptyView() }
                }
            }
        }
    }
}
