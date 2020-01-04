package com.maxfour.libreplayer.dagger.module

import android.content.Context
import com.maxfour.libreplayer.mvp.presenter.AlbumDetailsPresenter
import com.maxfour.libreplayer.mvp.presenter.AlbumDetailsPresenter.AlbumDetailsPresenterImpl
import com.maxfour.libreplayer.mvp.presenter.AlbumsPresenter
import com.maxfour.libreplayer.mvp.presenter.AlbumsPresenter.AlbumsPresenterImpl
import com.maxfour.libreplayer.mvp.presenter.ArtistDetailsPresenter
import com.maxfour.libreplayer.mvp.presenter.ArtistDetailsPresenter.ArtistDetailsPresenterImpl
import com.maxfour.libreplayer.mvp.presenter.ArtistsPresenter
import com.maxfour.libreplayer.mvp.presenter.ArtistsPresenter.ArtistsPresenterImpl
import com.maxfour.libreplayer.mvp.presenter.GenreDetailsPresenter
import com.maxfour.libreplayer.mvp.presenter.GenreDetailsPresenter.GenreDetailsPresenterImpl
import com.maxfour.libreplayer.mvp.presenter.GenresPresenter
import com.maxfour.libreplayer.mvp.presenter.GenresPresenter.GenresPresenterImpl
import com.maxfour.libreplayer.mvp.presenter.HomePresenter
import com.maxfour.libreplayer.mvp.presenter.HomePresenter.HomePresenterImpl
import com.maxfour.libreplayer.mvp.presenter.PlaylistSongsPresenter
import com.maxfour.libreplayer.mvp.presenter.PlaylistSongsPresenter.PlaylistSongsPresenterImpl
import com.maxfour.libreplayer.mvp.presenter.PlaylistsPresenter
import com.maxfour.libreplayer.mvp.presenter.PlaylistsPresenter.PlaylistsPresenterImpl
import com.maxfour.libreplayer.mvp.presenter.SearchPresenter
import com.maxfour.libreplayer.mvp.presenter.SearchPresenter.SearchPresenterImpl
import com.maxfour.libreplayer.mvp.presenter.SongPresenter
import com.maxfour.libreplayer.mvp.presenter.SongPresenter.SongPresenterImpl
import com.maxfour.libreplayer.providers.RepositoryImpl
import com.maxfour.libreplayer.providers.interfaces.Repository
import dagger.Module
import dagger.Provides

@Module
class PresenterModule {

    @Provides
    fun providesRepository(context: Context): Repository {
        return RepositoryImpl(context)
    }

    @Provides
    fun providesAlbumsPresenter(presenter: AlbumsPresenterImpl): AlbumsPresenter {
        return presenter
    }

    @Provides
    fun providesAlbumDetailsPresenter(presenter: AlbumDetailsPresenterImpl): AlbumDetailsPresenter {
        return presenter
    }

    @Provides
    fun providesArtistDetailsPresenter(presenter: ArtistDetailsPresenterImpl): ArtistDetailsPresenter {
        return presenter
    }

    @Provides
    fun providesArtistsPresenter(presenter: ArtistsPresenterImpl): ArtistsPresenter {
        return presenter
    }

    @Provides
    fun providesGenresPresenter(presenter: GenresPresenterImpl): GenresPresenter {
        return presenter
    }

    @Provides
    fun providesGenreDetailsPresenter(presenter: GenreDetailsPresenterImpl): GenreDetailsPresenter {
        return presenter
    }

    @Provides
    fun providesHomePresenter(presenter: HomePresenterImpl): HomePresenter {
        return presenter
    }

    @Provides
    fun providesPlaylistSongPresenter(presenter: PlaylistSongsPresenterImpl): PlaylistSongsPresenter {
        return presenter
    }

    @Provides
    fun providesPlaylistsPresenter(presenter: PlaylistsPresenterImpl): PlaylistsPresenter {
        return presenter
    }

    @Provides
    fun providesSearchPresenter(presenter: SearchPresenterImpl): SearchPresenter {
        return presenter
    }

    @Provides
    fun providesSongPresenter(presenter: SongPresenterImpl): SongPresenter {
        return presenter
    }
}