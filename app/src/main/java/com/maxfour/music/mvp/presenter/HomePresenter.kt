package com.maxfour.music.mvp.presenter

import com.maxfour.music.Result
import com.maxfour.music.model.Home
import com.maxfour.music.mvp.BaseView
import com.maxfour.music.mvp.Presenter
import com.maxfour.music.mvp.PresenterImpl
import com.maxfour.music.providers.interfaces.Repository
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
    add(disposable)
}

interface HomeView : BaseView {
    fun sections(sections: ArrayList<Home>)
}

interface HomePresenter : Presenter<HomeView> {
    fun loadSections()

    class HomePresenterImpl @Inject constructor(
            private val repository: Repository
    ) : PresenterImpl<HomeView>(), HomePresenter, CoroutineScope {
        private val job = Job()

        override val coroutineContext: CoroutineContext
            get() = Dispatchers.IO + job

        override fun detachView() {
            super.detachView()
            job.cancel()
        }

        override fun loadSections() {
            launch {
                val list = ArrayList<Home>()
                val recentArtistResult = listOf(
                        repository.topArtists(),
                        repository.topAlbums(),
                        repository.recentArtists(),
                        repository.recentAlbums(),
                        repository.favoritePlaylist()
                )
                for (r in recentArtistResult) {
                    when (r) {
                        is Result.Success -> list.add(r.data)
                    }
                }
                withContext(Dispatchers.Main) {
                    if (list.isNotEmpty()) view?.sections(list) else view?.showEmptyView()
                }
            }
        }
    }
}