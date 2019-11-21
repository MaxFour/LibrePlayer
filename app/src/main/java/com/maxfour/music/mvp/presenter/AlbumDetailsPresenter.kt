package com.maxfour.music.mvp.presenter

import com.maxfour.music.model.Album
import com.maxfour.music.model.Artist
import com.maxfour.music.mvp.Presenter
import com.maxfour.music.mvp.PresenterImpl
import com.maxfour.music.providers.interfaces.Repository
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

interface AlbumDetailsView {
    fun album(album: Album)

    fun complete()

    fun loadArtistImage(artist: Artist)

    fun moreAlbums(albums: ArrayList<Album>)
}

interface AlbumDetailsPresenter : Presenter<AlbumDetailsView> {
    fun loadAlbum(albumId: Int)

    fun loadMore(artistId: Int)

    class AlbumDetailsPresenterImpl @Inject constructor(
            private val repository: Repository
    ) : PresenterImpl<AlbumDetailsView>(), AlbumDetailsPresenter {

        private lateinit var album: Album
        private var disposable: CompositeDisposable = CompositeDisposable()
        override fun loadMore(artistId: Int) {
            disposable += repository.getArtistByIdFlowable(artistId)
                    .map {
                        view?.loadArtistImage(it)
                        return@map it.albums
                    }
                    .map {
                        it.filter { filterAlbum -> album.id != filterAlbum.id }
                    }
                    .subscribe({
                        if (it.isEmpty()) {
                            return@subscribe
                        }
                        view?.moreAlbums(ArrayList(it))
                    }, { t -> println(t) })
        }


        override fun loadAlbum(albumId: Int) {
            disposable += repository.getAlbumFlowable(albumId)
                    .doOnComplete {
                        view?.complete()
                    }
                    .subscribe({
                        album = it
                        view?.album(it)
                    }, { t -> println(t) })
        }

        override fun detachView() {
            super.detachView()
            disposable.dispose()
        }
    }
}