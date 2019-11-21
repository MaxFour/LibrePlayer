package com.maxfour.music.providers

import android.content.Context
import com.maxfour.music.R
import com.maxfour.music.Result
import com.maxfour.music.Result.Error
import com.maxfour.music.Result.Success
import com.maxfour.music.adapter.HomeAdapter
import com.maxfour.music.loaders.*
import com.maxfour.music.model.*
import com.maxfour.music.providers.interfaces.Repository
import com.maxfour.music.rest.LastFMRestClient
import com.maxfour.music.rest.model.LastFmArtist
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class RepositoryImpl(private val context: Context) : Repository {

    override suspend fun allAlbums(): Result<ArrayList<Album>> {
        return try {
            val albums = AlbumLoader.getAllAlbums(context)
            if (albums.isNotEmpty()) {
                Success(albums)
            } else {
                Error(Throwable("No items found"))
            }
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun allArtists(): Result<ArrayList<Artist>> {
        return try {
            val artists = ArtistLoader.getAllArtists(context)
            if (artists.isNotEmpty()) {
                Success(artists)
            } else {
                Error(Throwable("No items found"))
            }
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun allPlaylists(): Result<ArrayList<Playlist>> {
        return try {
            val playlists = PlaylistLoader.getAllPlaylists(context)
            if (playlists.isNotEmpty()) {
                Success(playlists)
            } else {
                Error(Throwable("No items found"))
            }
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun allGenres(): Result<ArrayList<Genre>> {
        return try {
            val genres = GenreLoader.getAllGenres(context)
            if (genres.isNotEmpty()) {
                Success(genres)
            } else {
                Error(Throwable("No items found"))
            }
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun search(query: String?): Result<MutableList<Any>> {
        return try {
            val result = SearchLoader.searchAll(context, query)
            if (result.isNotEmpty()) {
                Success(result)
            } else {
                Error(Throwable("No items found"))
            }
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun allSongs(): Result<ArrayList<Song>> {
        return try {
            val songs = SongLoader.getAllSongs(context);
            if (songs.isEmpty()) {
                Error(Throwable("No items found"))
            } else {
                Success(songs);
            }
        } catch (e: Exception) {
            Error(e);
        }
    }

    override suspend fun getPlaylistSongs(playlist: Playlist): Result<ArrayList<Song>> {
        return try {
            val songs: ArrayList<Song> = if (playlist is AbsCustomPlaylist) {
                playlist.getSongs(context)
            } else {
                PlaylistSongsLoader.getPlaylistSongList(context, playlist.id)
            }
            Success(songs);
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun getGenre(genreId: Int): Result<ArrayList<Song>> {
        return try {
            val songs = GenreLoader.getSongs(context, genreId)
            if (songs.isEmpty()) {
                Error(Throwable("No items found"))
            } else {
                Success(songs);
            }
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun recentArtists(): Result<Home> {
        return try {
            val artists = LastAddedSongsLoader.getLastAddedArtists(context)
            if (artists.isEmpty()) {
                Error(Throwable("No items found"))
            } else {
                Success(Home(0,
                        R.string.recent_artists,
                        R.string.recent_added_artists,
                        artists,
                        HomeAdapter.RECENT_ARTISTS,
                        R.drawable.ic_artist_white_24dp))
            }
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun recentAlbums(): Result<Home> {
        return try {
            val albums = LastAddedSongsLoader.getLastAddedAlbums(context)
            if (albums.isEmpty()) {
                Error(Throwable("No items found"))
            } else {
                Success(Home(1,
                        R.string.recent_albums,
                        R.string.recent_added_albums,
                        albums,
                        HomeAdapter.RECENT_ALBUMS,
                        R.drawable.ic_album_white_24dp
                ));
            }
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun topAlbums(): Result<Home> {
        return try {
            val albums = TopAndRecentlyPlayedTracksLoader.getTopAlbums(context)
            if (albums.isEmpty()) {
                Error(Throwable("No items found"))
            } else {
                Success(Home(3,
                        R.string.top_albums,
                        R.string.most_played_albums,
                        albums,
                        HomeAdapter.TOP_ALBUMS,
                        R.drawable.ic_album_white_24dp
                ));
            }
        } catch (e: Exception) {
            Error(e)
        }
    }


    override suspend fun topArtists(): Result<Home> {
        return try {
            val artists = TopAndRecentlyPlayedTracksLoader.getTopArtists(context)
            if (artists.isEmpty()) {
                Error(Throwable("No items found"))
            } else {
                Success(Home(2,
                        R.string.top_artists,
                        R.string.most_played_artists,
                        artists,
                        HomeAdapter.TOP_ARTISTS,
                        R.drawable.ic_artist_white_24dp
                ));
            }
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun favoritePlaylist(): Result<Home> {
        return try {
            val playlists = PlaylistLoader.getFavoritePlaylist(context)
            if (playlists.isEmpty()) {
                Error(Throwable("No items found"))
            } else {
                Success(Home(4,
                        R.string.favorites,
                        R.string.favorites_songs,
                        playlists,
                        HomeAdapter.PLAYLISTS,
                        R.drawable.ic_favorite_white_24dp
                ));
            }
        } catch (e: Exception) {
            Error(e)
        }
    }

    override fun artistInfoFloable(
            name: String,
            lang: String?,
            cache: String?
    ): Observable<LastFmArtist> {
        return LastFMRestClient(context).apiService.getArtistInfoFloable(name, lang, cache)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }


    override fun getSongFlowable(id: Int): Observable<Song> {
        return SongLoader.getSongFlowable(context, id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getAlbumFlowable(albumId: Int): Observable<Album> {
        return AlbumLoader.getAlbumFlowable(context, albumId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getArtistByIdFlowable(artistId: Int): Observable<Artist> {
        return ArtistLoader.getArtistFlowable(context, artistId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getPlaylistSongsFlowable(playlist: Playlist): Observable<ArrayList<Song>> {
        return PlaylistSongsLoader.getPlaylistSongListFlowable(context, playlist)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getGenreFlowable(genreId: Int): Observable<ArrayList<Song>> {
        return GenreLoader.getSongsFlowable(context, genreId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }


    override val favoritePlaylistFlowable: Observable<ArrayList<Playlist>>
        get() = PlaylistLoader.getFavoritePlaylistFlowable(context)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())


    override val allSongsFlowable: Observable<ArrayList<Song>>
        get() = SongLoader.getAllSongsFlowable(context)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

    override val suggestionSongsFlowable: Observable<ArrayList<Song>>
        get() = SongLoader.suggestSongs(context)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

    override val allAlbumsFlowable: Observable<ArrayList<Album>>
        get() = AlbumLoader.getAllAlbumsFlowable(context)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

    override val recentAlbumsFlowable: Observable<ArrayList<Album>>
        get() = LastAddedSongsLoader.getLastAddedAlbumsFlowable(context)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

    override val topAlbumsFlowable: Observable<ArrayList<Album>>
        get() = TopAndRecentlyPlayedTracksLoader.getTopAlbumsFlowable(context)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

    override val allArtistsFlowable: Observable<ArrayList<Artist>>
        get() = ArtistLoader.getAllArtistsFlowable(context)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

    override val recentArtistsFlowable: Observable<ArrayList<Artist>>
        get() = LastAddedSongsLoader.getLastAddedArtistsFlowable(context)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

    override val topArtistsFlowable: Observable<ArrayList<Artist>>
        get() = TopAndRecentlyPlayedTracksLoader.getTopArtistsFlowable(context)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

    override val allPlaylistsFlowable: Observable<ArrayList<Playlist>>
        get() = PlaylistLoader.getAllPlaylistsFlowoable(context)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())


    override val allGenresFlowable: Observable<ArrayList<Genre>>
        get() = GenreLoader.getAllGenresFlowable(context)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

    override fun getSong(id: Int): Song {
        return SongLoader.getSong(context, id)
    }

    override fun getAlbum(albumId: Int): Album {
        return AlbumLoader.getAlbum(context, albumId)
    }

    override fun getArtistById(artistId: Long): Artist {
        return ArtistLoader.getArtist(context, artistId.toInt())
    }


}
