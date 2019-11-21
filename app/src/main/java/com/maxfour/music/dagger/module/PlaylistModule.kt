package com.maxfour.music.dagger.module

import com.maxfour.music.mvp.presenter.PlaylistSongsPresenter
import com.maxfour.music.mvp.presenter.PlaylistSongsPresenter.PlaylistSongsPresenterImpl
import com.maxfour.music.mvp.presenter.PlaylistsPresenter
import com.maxfour.music.mvp.presenter.PlaylistsPresenter.PlaylistsPresenterImpl
import dagger.Module
import dagger.Provides

@Module
class PlaylistModule {
    @Provides
    fun providesPlaylistSongPresenter(presenter: PlaylistSongsPresenterImpl): PlaylistSongsPresenter {
        return presenter
    }

    @Provides
    fun providesPlaylistsPresenter(presenter: PlaylistsPresenterImpl): PlaylistsPresenter {
        return presenter
    }
}
