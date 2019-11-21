package com.maxfour.music.mvp.presenter

import com.maxfour.music.Result
import com.maxfour.music.model.Song
import com.maxfour.music.mvp.BaseView
import com.maxfour.music.mvp.Presenter
import com.maxfour.music.mvp.PresenterImpl
import com.maxfour.music.providers.interfaces.Repository
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
