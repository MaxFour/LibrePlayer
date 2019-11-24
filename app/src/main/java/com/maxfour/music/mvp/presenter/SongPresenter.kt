package com.maxfour.music.mvp.presenter

import com.maxfour.music.Result
import com.maxfour.music.model.Song
import com.maxfour.music.mvp.*
import com.maxfour.music.providers.interfaces.Repository
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

interface SongView {
      fun songs(songs: ArrayList<Song>)

      fun showEmptyView()
}

interface SongPresenter : Presenter<SongView> {
      fun loadSongs()
      class SongPresenterImpl @Inject constructor(
              private val repository: Repository
      ) : PresenterImpl<SongView>(), SongPresenter, CoroutineScope {

          private var job: Job = Job()

          override val coroutineContext: CoroutineContext
              get() = Dispatchers.IO + job

          override fun loadSongs() {
              launch {
                  when (val songs = repository.allSongs()) {
                      is Result.Success -> withContext(Dispatchers.Main) { view?.songs(songs.data) }
                      is Result.Error   -> withContext(Dispatchers.Main) { view?.showEmptyView() }
                  }
              }
          }

          override fun detachView() {
              super.detachView()
              job.cancel();
          }
      }
}


