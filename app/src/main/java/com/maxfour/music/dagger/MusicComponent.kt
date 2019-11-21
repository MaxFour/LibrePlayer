package com.maxfour.music.dagger

import com.maxfour.music.activities.*
import com.maxfour.music.dagger.module.*
import com.maxfour.music.fragments.mainactivity.*
import com.maxfour.music.fragments.mainactivity.home.BannerHomeFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    RepositoryModule::class,
    AlbumModule::class,
    ArtistModule::class,
    GenreModule::class,
    HomeModule::class,
    PlaylistModule::class,
    SearchModule::class,
    SongModule::class,
    ActivityModule::class
])
interface MusicComponent {

    fun inject(songsFragment: SongsFragment)

    fun inject(albumsFragment: AlbumsFragment)

    fun inject(artistsFragment: ArtistsFragment)

    fun inject(genresFragment: GenresFragment)

    fun inject(playlistsFragment: PlaylistsFragment)

    fun inject(artistDetailActivity: ArtistDetailActivity)

    fun inject(albumDetailsActivity: AlbumDetailsActivity)

    fun inject(playlistDetailActivity: PlaylistDetailActivity)

    fun inject(genreDetailsActivity: GenreDetailsActivity)

    fun inject(searchActivity: SearchActivity)

    fun inject(bannerHomeFragment: BannerHomeFragment)
}
