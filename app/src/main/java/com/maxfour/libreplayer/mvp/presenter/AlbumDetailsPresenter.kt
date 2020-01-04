package com.maxfour.libreplayer.mvp.presenter

import com.maxfour.libreplayer.Result
import com.maxfour.libreplayer.Result.Success
import com.maxfour.libreplayer.model.Album
import com.maxfour.libreplayer.model.Artist
import com.maxfour.libreplayer.mvp.Presenter
import com.maxfour.libreplayer.mvp.PresenterImpl
import com.maxfour.libreplayer.providers.interfaces.Repository
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

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
    ) : PresenterImpl<AlbumDetailsView>(), AlbumDetailsPresenter, CoroutineScope {
        private val job = Job()
        private lateinit var album: Album

        override fun loadMore(artistId: Int) {
            launch {
                when (val result = repository.artistById(artistId)) {
                    is Success -> withContext(Dispatchers.Main) { showArtistImage(result.data) }
                    is Result.Error -> withContext(Dispatchers.Main) {}
                }
            }
        }

        private fun showArtistImage(artist: Artist) {
            view?.loadArtistImage(artist)

            artist.albums?.filter { it.id != album.id }?.let {
                if (it.isNotEmpty()) view?.moreAlbums(ArrayList(it))
            }
        }

        override fun loadAlbum(albumId: Int) {
            launch {
                when (val result = repository.albumById(albumId)) {
                    is Success -> withContext(Dispatchers.Main) {
                        album = result.data
                        view?.album(result.data)
                    }
                    is Error -> withContext(Dispatchers.Main) { view?.complete() }
                }
            }
        }

        override fun detachView() {
            super.detachView()
            job.cancel()
        }

        override val coroutineContext: CoroutineContext
            get() = Dispatchers.IO + job
    }
}
