package com.maxfour.music.mvp.presenter

import com.maxfour.music.Result.Error
import com.maxfour.music.Result.Success
import com.maxfour.music.mvp.BaseView
import com.maxfour.music.mvp.Presenter
import com.maxfour.music.mvp.PresenterImpl
import com.maxfour.music.providers.interfaces.Repository
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

interface SearchView : BaseView {
    fun showData(data: MutableList<Any>)
}

interface SearchPresenter : Presenter<SearchView> {

    fun search(query: String?)

    class SearchPresenterImpl @Inject constructor(
            private val repository: Repository
    ) : PresenterImpl<SearchView>(), SearchPresenter, CoroutineScope {

        override val coroutineContext: CoroutineContext
            get() = Dispatchers.IO + job

        private var job: Job = Job()

        override fun detachView() {
            super.detachView()
            job.cancel()
        }

        override fun search(query: String?) {
            launch {
                when (val result = repository.search(query)) {
                    is Success -> withContext(Dispatchers.Main) {
                        view?.showData(result.data)
                    }
                    is Error -> withContext(Dispatchers.Main) { view?.showEmptyView() }
                }
            }
        }
    }
}


