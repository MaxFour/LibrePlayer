package com.maxfour.music.loaders

import android.content.Context
import android.provider.MediaStore.Audio.AudioColumns
import com.maxfour.music.model.Album
import com.maxfour.music.model.Artist
import com.maxfour.music.util.PreferenceUtil
import io.reactivex.Observable

object ArtistLoader {
    private fun getSongLoaderSortOrder(context: Context): String {
        return PreferenceUtil.getInstance(context).artistSortOrder + ", " +
                PreferenceUtil.getInstance(context).artistAlbumSortOrder + ", " +
                PreferenceUtil.getInstance(context).albumDetailSongSortOrder + ", " +
                PreferenceUtil.getInstance(context).artistDetailSongSortOrder
    }

    fun getAllArtistsFlowable(
            context: Context
    ): Observable<ArrayList<Artist>> {
        return Observable.create { e ->
            SongLoader.getSongsFlowable(SongLoader.makeSongCursor(
                    context, null, null,
                    getSongLoaderSortOrder(context))
            ).subscribe { songs ->
                e.onNext(splitIntoArtists(AlbumLoader.splitIntoAlbums(songs)))
                e.onComplete()
            }
        }
    }

    fun getAllArtists(context: Context): ArrayList<Artist> {
        val songs = SongLoader.getSongs(SongLoader.makeSongCursor(
                context,
                null, null,
                getSongLoaderSortOrder(context))
        )
        return splitIntoArtists(AlbumLoader.splitIntoAlbums(songs))
    }

    fun getArtistsFlowable(context: Context, query: String): Observable<ArrayList<Artist>> {
        return Observable.create { e ->
            SongLoader.getSongsFlowable(SongLoader.makeSongCursor(
                    context,
                    AudioColumns.ARTIST + " LIKE ?",
                    arrayOf("%$query%"),
                    getSongLoaderSortOrder(context))
            ).subscribe { songs ->
                e.onNext(splitIntoArtists(AlbumLoader.splitIntoAlbums(songs)))
                e.onComplete()
            }
        }
    }

    fun getArtists(context: Context, query: String): ArrayList<Artist> {
        val songs = SongLoader.getSongs(SongLoader.makeSongCursor(
                context,
                AudioColumns.ARTIST + " LIKE ?",
                arrayOf("%$query%"),
                getSongLoaderSortOrder(context))
        )
        return splitIntoArtists(AlbumLoader.splitIntoAlbums(songs))
    }

    fun splitIntoArtists(albums: ArrayList<Album>?): ArrayList<Artist> {
        val artists = ArrayList<Artist>()
        if (albums != null) {
            for (album in albums) {
                getOrCreateArtist(artists, album.artistId).albums!!.add(album)
            }
        }
        return artists
    }

    private fun getOrCreateArtist(artists: ArrayList<Artist>, artistId: Int): Artist {
        for (artist in artists) {
            if (artist.albums!!.isNotEmpty() && artist.albums[0].songs!!.isNotEmpty() && artist.albums[0].songs!![0].artistId == artistId) {
                return artist
            }
        }
        val album = Artist()
        artists.add(album)
        return album
    }

    fun splitIntoArtists(albums: Observable<ArrayList<Album>>): Observable<ArrayList<Artist>> {
        return Observable.create { e ->
            val artists = ArrayList<Artist>()
            albums.subscribe { localAlbums ->
                if (localAlbums != null) {
                    for (album in localAlbums) {
                        getOrCreateArtist(artists, album.artistId).albums!!.add(album)
                    }
                }
                e.onNext(artists)
                e.onComplete()
            }
        }
    }

    fun getArtistFlowable(context: Context, artistId: Int): Observable<Artist> {
        return Observable.create { e ->
            SongLoader.getSongsFlowable(SongLoader.makeSongCursor(context, AudioColumns.ARTIST_ID + "=?",
                    arrayOf(artistId.toString()),
                    getSongLoaderSortOrder(context)))
                    .subscribe { songs ->
                        val artist = Artist(AlbumLoader.splitIntoAlbums(songs))
                        e.onNext(artist)
                        e.onComplete()
                    }
        }
    }

    fun getArtist(context: Context, artistId: Int): Artist {
        val songs = SongLoader.getSongs(SongLoader.makeSongCursor(
                context,
                AudioColumns.ARTIST_ID + "=?",
                arrayOf(artistId.toString()),
                getSongLoaderSortOrder(context))
        )
        return Artist(AlbumLoader.splitIntoAlbums(songs))
    }
}
