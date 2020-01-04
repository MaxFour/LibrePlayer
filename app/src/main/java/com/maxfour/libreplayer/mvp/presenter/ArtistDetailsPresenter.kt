package com.maxfour.libreplayer.mvp.presenter

import com.maxfour.libreplayer.Result
import com.maxfour.libreplayer.model.Artist
import com.maxfour.libreplayer.mvp.BaseView
import com.maxfour.libreplayer.mvp.Presenter
import com.maxfour.libreplayer.mvp.PresenterImpl
import com.maxfour.libreplayer.providers.interfaces.Repository
import com.maxfour.libreplayer.rest.model.LastFmArtist
import kotlinx.coroutines.*
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

interface ArtistDetailsView : BaseView {
    fun artist(artist: Artist)
    fun artistInfo(lastFmArtist: LastFmArtist?)
    fun complete()
}

interface ArtistDetailsPresenter : Presenter<ArtistDetailsView> {

    fun loadArtist(artistId: Int)

    fun loadBiography(
            name: String, lang: String? = Locale.getDefault().language, cache: String?
    )

    class ArtistDetailsPresenterImpl @Inject constructor(
            private val repository: Repository
    ) : PresenterImpl<ArtistDetailsView>(), ArtistDetailsPresenter, CoroutineScope {
        override val coroutineContext: CoroutineContext
            get() = Dispatchers.IO + job

        private val job = Job()

        override fun loadBiography(name: String, lang: String?, cache: String?) {
            launch {
                when (val result = repository.artistInfo(name, lang, cache)) {
                    is Result.Success -> withContext(Dispatchers.Main) {
                        view?.artistInfo(result.data)
                    }
                    is Result.Error -> withContext(Dispatchers.Main) {
                    }
                }
            }
        }

        override fun loadArtist(artistId: Int) {
            launch {
                when (val result = repository.artistById(artistId)) {
                    is Result.Success -> withContext(Dispatchers.Main) {
                        view?.artist(result.data)

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
