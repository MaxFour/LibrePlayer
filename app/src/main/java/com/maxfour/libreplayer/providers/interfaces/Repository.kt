package com.maxfour.libreplayer.providers.interfaces

import com.maxfour.libreplayer.Result
import com.maxfour.libreplayer.model.*
import com.maxfour.libreplayer.rest.model.LastFmArtist

interface Repository {

    suspend fun allAlbums(): Result<ArrayList<Album>>

    suspend fun albumById(albumId: Int): Result<Album>

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

    suspend fun artistInfo(name: String, lang: String?, cache: String?): Result<LastFmArtist>

    suspend fun artistById(artistId: Int): Result<Artist>
}
