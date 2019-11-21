package com.maxfour.music.dagger.module

import com.maxfour.music.mvp.presenter.ArtistDetailsPresenter
import com.maxfour.music.mvp.presenter.ArtistDetailsPresenter.ArtistDetailsPresenterImpl
import com.maxfour.music.mvp.presenter.ArtistsPresenter
import com.maxfour.music.mvp.presenter.ArtistsPresenter.ArtistsPresenterImpl
import dagger.Module
import dagger.Provides

@Module
class ArtistModule {

    @Provides
    fun providesArtistDetailsPresenter(presenter: ArtistDetailsPresenterImpl): ArtistDetailsPresenter {
        return presenter
    }

    @Provides
    fun providesArtistsPresenter(presenter: ArtistsPresenterImpl): ArtistsPresenter {
        return presenter
    }
}
