package com.maxfour.music.dagger.module

import com.maxfour.music.mvp.presenter.AlbumDetailsPresenter
import com.maxfour.music.mvp.presenter.AlbumDetailsPresenter.AlbumDetailsPresenterImpl
import com.maxfour.music.mvp.presenter.AlbumsPresenter
import com.maxfour.music.mvp.presenter.AlbumsPresenter.AlbumsPresenterImpl
import dagger.Module
import dagger.Provides

@Module
class AlbumModule {

    @Provides
    fun providesAlbumsPresenter(presenter: AlbumsPresenterImpl): AlbumsPresenter {
        return presenter
    }

    @Provides
    fun providesAlbumDetailsPresenter(presenter: AlbumDetailsPresenterImpl): AlbumDetailsPresenter {
        return presenter
    }
}
