package com.maxfour.music.providers.interfaces

import com.maxfour.music.Result
import com.maxfour.music.model.*
import com.maxfour.music.rest.model.LastFmArtist
import io.reactivex.Observable

interface Repository {

    suspend fun allAlbums(): Result<ArrayList<Album>>

    suspend fun allSongs(): Result<ArrayList<Song>>

    suspend fun allArtists(): Result<ArrayList<Artist>>

    suspend fun allPlaylists(): Result<ArrayList<Playlist>>

    suspend fun allGenres(): Result<ArrayList<Genre>>

    suspend fun search(query: String?): Result<MutableList<Any>>

    suspend fun getPlaylistSongs(playlist: Playlist): Result<ArrayList<Song>>

    suspend fun getGenre(genreId: Int): Result<ArrayList<Song>>

    suspend fun recentArtists(): Result<Home>

    suspend fun topArtists(): Result<Home>

    suspend fun topAlbums(): Result<Home>

    suspend fun recentAlbums(): Result<Home>

    suspend fun favoritePlaylist(): Result<Home>

    val allSongsFlowable: Observable<ArrayList<Song>>

    val suggestionSongsFlowable: Observable<ArrayList<Song>>

    val allAlbumsFlowable: Observable<ArrayList<Album>>

    val recentAlbumsFlowable: Observable<ArrayList<Album>>

    val topAlbumsFlowable: Observable<ArrayList<Album>>

    val allArtistsFlowable: Observable<ArrayList<Artist>>

    val recentArtistsFlowable: Observable<ArrayList<Artist>>

    val topArtistsFlowable: Observable<ArrayList<Artist>>

    val allPlaylistsFlowable: Observable<ArrayList<Playlist>>

    val allGenresFlowable: Observable<ArrayList<Genre>>

    fun getSongFlowable(id: Int): Observable<Song>

    fun getSong(id: Int): Song

    fun getAlbumFlowable(albumId: Int): Observable<Album>

    fun getAlbum(albumId: Int): Album

    fun getArtistByIdFlowable(artistId: Int): Observable<Artist>

    fun getArtistById(artistId: Long): Artist

    fun getPlaylistSongsFlowable(playlist: Playlist): Observable<ArrayList<Song>>

    fun getGenreFlowable(genreId: Int): Observable<ArrayList<Song>>


    val favoritePlaylistFlowable: Observable<ArrayList<Playlist>>


    fun artistInfoFloable(name: String,
                          lang: String?,
                          cache: String?): Observable<LastFmArtist>
}