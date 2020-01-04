package com.maxfour.libreplayer.providers

import android.content.Context
import com.maxfour.libreplayer.R
import com.maxfour.libreplayer.Result
import com.maxfour.libreplayer.Result.Error
import com.maxfour.libreplayer.Result.Success
import com.maxfour.libreplayer.adapter.HomeAdapter
import com.maxfour.libreplayer.loaders.*
import com.maxfour.libreplayer.model.*
import com.maxfour.libreplayer.providers.interfaces.Repository
import com.maxfour.libreplayer.rest.LastFMRestClient
import com.maxfour.libreplayer.rest.model.LastFmArtist
import java.io.IOException
import javax.inject.Inject

class RepositoryImpl @Inject constructor(private val context: Context) : Repository {

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

    override suspend fun albumById(albumId: Int): Result<Album> {
        return try {
            val album = AlbumLoader.getAlbum(context, albumId)
            if (album != null) {
                Success(album)
            } else {
                Error(Throwable("No album"))
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
            val songs = SongLoader.getAllSongs(context)
            if (songs.isEmpty()) {
                Error(Throwable("No items found"))
            } else {
                Success(songs)
            }
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun getPlaylistSongs(playlist: Playlist): Result<ArrayList<Song>> {
        return try {
            val songs: ArrayList<Song> = if (playlist is AbsCustomPlaylist) {
                playlist.getSongs(context)
            } else {
                PlaylistSongsLoader.getPlaylistSongList(context, playlist.id)
            }
            Success(songs)
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
                Success(songs)
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
                Success(
                    Home(
                        0,
                        R.string.recent_artists,
                        artists,
                        HomeAdapter.RECENT_ARTISTS,
                        R.drawable.ic_artist_white_24dp
                    )
                )
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
                Success(
                    Home(
                        1,
                        R.string.recent_albums,
                        albums,
                        HomeAdapter.RECENT_ALBUMS,
                        R.drawable.ic_album_white_24dp
                    )
                )
            }
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun topAlbums(): Result<Home> {
        return try {
            val albums = TopAndRecentlyPlayedSongsLoader.getTopAlbums(context)
            if (albums.isEmpty()) {
                Error(Throwable("No items found"))
            } else {
                Success(
                    Home(
                        3,
                        R.string.top_albums,
                        albums,
                        HomeAdapter.TOP_ALBUMS,
                        R.drawable.ic_album_white_24dp
                    )
                )
            }
        } catch (e: Exception) {
            Error(e)
        }
    }


    override suspend fun topArtists(): Result<Home> {
        return try {
            val artists = TopAndRecentlyPlayedSongsLoader.getTopArtists(context)
            if (artists.isEmpty()) {
                Error(Throwable("No items found"))
            } else {
                Success(
                    Home(
                        2,
                        R.string.top_artists,
                        artists,
                        HomeAdapter.TOP_ARTISTS,
                        R.drawable.ic_artist_white_24dp
                    )
                )
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
                Success(
                    Home(
                        4,
                        R.string.favorites,
                        playlists,
                        HomeAdapter.PLAYLISTS,
                        R.drawable.ic_favorite_white_24dp
                    )
                )
            }
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun artistInfo(
        name: String,
        lang: String?,
        cache: String?
    ):  Result<LastFmArtist> = safeApiCall(
        call = {
            Success(LastFMRestClient(context).apiService.artistInfo(name, lang, cache))
        },
        errorMessage = "Error"
    )

    override suspend fun artistById(artistId: Int): Result<Artist> {
        return try {
            val artist = ArtistLoader.getArtist(context, artistId)
            return Success(artist)
        } catch (e: Exception) {
            Error(Throwable("Error loading artist"))
        }
    }
}

suspend fun <T : Any> safeApiCall(call: suspend () -> Result<T>, errorMessage: String): Result<T> = try {
    call.invoke()
} catch (e: Exception) {
    Error(IOException(errorMessage, e))
}
