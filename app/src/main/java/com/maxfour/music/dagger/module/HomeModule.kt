package com.maxfour.music.dagger.module

import com.maxfour.music.mvp.presenter.HomePresenter
import com.maxfour.music.mvp.presenter.HomePresenter.HomePresenterImpl
import dagger.Module
import dagger.Provides

@Module
class HomeModule {

    @Provides
    fun providesHomePresenter(presenter: HomePresenterImpl): HomePresenter {
        return presenter
    }

}
