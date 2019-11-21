package com.maxfour.music.dagger.module

import com.maxfour.music.mvp.presenter.SongPresenter
import com.maxfour.music.mvp.presenter.SongPresenter.SongPresenterImpl
import dagger.Module
import dagger.Provides

@Module
class SongModule {
    @Provides
    fun providesSongPresenter(presenter: SongPresenterImpl): SongPresenter {
        return presenter
    }
}
