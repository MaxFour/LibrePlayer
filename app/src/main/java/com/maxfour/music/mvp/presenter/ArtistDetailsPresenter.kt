package com.maxfour.music.mvp.presenter

import com.maxfour.music.model.Artist
import com.maxfour.music.mvp.BaseView
import com.maxfour.music.mvp.Presenter
import com.maxfour.music.mvp.PresenterImpl
import com.maxfour.music.providers.interfaces.Repository
import com.maxfour.music.rest.model.LastFmArtist
import io.reactivex.disposables.CompositeDisposable
import java.util.*
import javax.inject.Inject

interface ArtistDetailsView : BaseView {
    fun artist(artist: Artist)
    fun artistInfo(lastFmArtist: LastFmArtist?)
    fun complete()
}

interface ArtistDetailsPresenter : Presenter<ArtistDetailsView> {

    fun loadArtist(artistId: Int)

    fun loadBiography(name: String,
                      lang: String? = Locale.getDefault().language,
                      cache: String?)

    class ArtistDetailsPresenterImpl @Inject constructor(
            private val repository: Repository
    ) : PresenterImpl<ArtistDetailsView>(), ArtistDetailsPresenter {

        override fun loadBiography(name: String,
                                   lang: String?,
                                   cache: String?) {
            disposable += repository.artistInfoFloable(name, lang, cache)
                    .subscribe {
                        view?.artistInfo(it)
                    }
        }

        private var disposable = CompositeDisposable()

        override fun loadArtist(artistId: Int) {
            disposable += repository.getArtistByIdFlowable(artistId)
                    .doOnComplete {
                        view?.complete()
                    }
                    .subscribe({
                        view?.artist(it)
                    }, { t -> println(t) })
        }

        override fun detachView() {
            super.detachView()
            disposable.dispose()
        }
    }
}