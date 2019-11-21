package com.maxfour.music.dagger.module

import com.maxfour.music.mvp.presenter.GenreDetailsPresenter
import com.maxfour.music.mvp.presenter.GenreDetailsPresenter.GenreDetailsPresenterImpl
import com.maxfour.music.mvp.presenter.GenresPresenter
import com.maxfour.music.mvp.presenter.GenresPresenter.GenresPresenterImpl
import dagger.Module
import dagger.Provides

@Module
class GenreModule {

    @Provides
    fun providesGenresPresenter(presenter: GenresPresenterImpl): GenresPresenter {
        return presenter
    }


    @Provides
    fun providesGenreDetailsPresenter(presenter: GenreDetailsPresenterImpl): GenreDetailsPresenter {
        return presenter
    }
}
