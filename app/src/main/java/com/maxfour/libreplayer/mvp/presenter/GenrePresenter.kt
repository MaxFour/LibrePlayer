package com.maxfour.libreplayer.mvp.presenter

import com.maxfour.libreplayer.Result
import com.maxfour.libreplayer.model.Genre
import com.maxfour.libreplayer.mvp.BaseView
import com.maxfour.libreplayer.mvp.Presenter
import com.maxfour.libreplayer.mvp.PresenterImpl
import com.maxfour.libreplayer.providers.interfaces.Repository
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

interface GenresView : BaseView {
    fun genres(genres: ArrayList<Genre>)
}

interface GenresPresenter : Presenter<GenresView> {
    fun loadGenres()

    class GenresPresenterImpl @Inject constructor(
            private val repository: Repository
    ) : PresenterImpl<GenresView>(), GenresPresenter, CoroutineScope {
        private val job = Job()

        override val coroutineContext: CoroutineContext
            get() = Dispatchers.IO + job

        override fun detachView() {
            super.detachView()
            job.cancel()
        }

        override fun loadGenres() {
            launch {
                when (val result = repository.allGenres()) {
                    is Result.Success -> withContext(Dispatchers.Main) {
                        view?.genres(result.data)
                    }
                    is Result.Error -> withContext(Dispatchers.Main) { view?.showEmptyView() }
                }
            }
        }
    }
}
