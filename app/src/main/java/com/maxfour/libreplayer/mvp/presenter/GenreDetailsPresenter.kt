package com.maxfour.libreplayer.mvp.presenter

import com.maxfour.libreplayer.Result
import com.maxfour.libreplayer.model.Song
import com.maxfour.libreplayer.mvp.BaseView
import com.maxfour.libreplayer.mvp.Presenter
import com.maxfour.libreplayer.mvp.PresenterImpl
import com.maxfour.libreplayer.providers.interfaces.Repository
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

interface GenreDetailsView : BaseView {
    fun songs(songs: ArrayList<Song>)
}

interface GenreDetailsPresenter : Presenter<GenreDetailsView> {
    fun loadGenreSongs(genreId: Int)

    class GenreDetailsPresenterImpl @Inject constructor(
            private val repository: Repository
    ) : PresenterImpl<GenreDetailsView>(), GenreDetailsPresenter, CoroutineScope {
        private val job = Job()

        override val coroutineContext: CoroutineContext
            get() = Dispatchers.IO + job

        override fun detachView() {
            super.detachView()
            job.cancel()
        }


        override fun loadGenreSongs(genreId: Int) {
            launch {
                when (val result = repository.getGenre(genreId)) {
                    is Result.Success -> withContext(Dispatchers.Main) {
                        view?.songs(result.data)
                    }
                    is Result.Error -> withContext(Dispatchers.Main) {
                        view?.showEmptyView()
                    }
                }
            }
        }
    }
}
