package com.maxfour.music.mvp.presenter

import com.maxfour.music.Result
import com.maxfour.music.model.Artist
import com.maxfour.music.mvp.BaseView
import com.maxfour.music.mvp.Presenter
import com.maxfour.music.mvp.PresenterImpl
import com.maxfour.music.providers.interfaces.Repository
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

interface ArtistsView : BaseView {
    fun artists(artists: ArrayList<Artist>)
}

interface ArtistsPresenter : Presenter<ArtistsView> {

    fun loadArtists()

    class ArtistsPresenterImpl @Inject constructor(
            private val repository: Repository
    ) : PresenterImpl<ArtistsView>(), ArtistsPresenter, CoroutineScope {
        private val job = Job()

        override val coroutineContext: CoroutineContext
            get() = Dispatchers.IO + job

        override fun detachView() {
            super.detachView()
            job.cancel()
        }

        override fun loadArtists() {
            launch {
                when (val result = repository.allArtists()) {
                    is Result.Success -> withContext(Dispatchers.Main) {
                        view?.artists(result.data)
                    }
                    is Result.Error -> withContext(Dispatchers.Main) { view?.showEmptyView() }
                }
            }
        }
    }
}
