package com.maxfour.music.dagger.module

import com.maxfour.music.mvp.presenter.SearchPresenter
import com.maxfour.music.mvp.presenter.SearchPresenter.SearchPresenterImpl
import dagger.Module
import dagger.Provides

@Module
class SearchModule {

    @Provides
    fun providesSearchPresenter(presenter: SearchPresenterImpl): SearchPresenter {
        return presenter
    }

}
