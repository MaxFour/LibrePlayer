package com.maxfour.music.loaders

import android.content.Context
import android.provider.MediaStore.Audio.AudioColumns
import com.maxfour.music.model.Album
import com.maxfour.music.model.Song
import com.maxfour.music.util.PreferenceUtil
import io.reactivex.Observable
import java.util.*
import kotlin.collections.ArrayList

object AlbumLoader {
    fun getAllAlbumsFlowable(
            context: Context
    ): Observable<ArrayList<Album>> {
        val songs = SongLoader.getSongsFlowable(
                SongLoader.makeSongCursor(
                        context, null, null,
                        getSongLoaderSortOrder(context))
        )

        return splitIntoAlbumsFlowable(songs)
    }

    fun getAlbumsFlowable(
            context: Context,
            query: String
    ): Observable<ArrayList<Album>> {
        val songs = SongLoader.getSongsFlowable(
                SongLoader.makeSongCursor(
                        context,
                        AudioColumns.ALBUM + " LIKE ?",
                        arrayOf("%$query%"),
                        getSongLoaderSortOrder(context))
        )
        return splitIntoAlbumsFlowable(songs)
    }

    fun getAlbums(
            context: Context,
            query: String
    ): ArrayList<Album> {
        val songs = SongLoader.getSongs(SongLoader.makeSongCursor(
                context,
                AudioColumns.ALBUM + " LIKE ?",
                arrayOf("%$query%"),
                getSongLoaderSortOrder(context))
        )
        return splitIntoAlbums(songs)
    }

    fun getAlbumFlowable(
            context: Context,
            albumId: Int
    ): Observable<Album> {
        return Observable.create { e ->
            val songs = SongLoader.getSongsFlowable(
                    SongLoader.makeSongCursor(
                            context,
                            AudioColumns.ALBUM_ID + "=?",
                            arrayOf(albumId.toString()),
                            getSongLoaderSortOrder(context)
                    )
            )
            songs.subscribe { songs1 ->
                e.onNext(Album(songs1))
                e.onComplete()
            }
        }
    }

    fun getAlbum(
            context: Context,
            albumId: Int
    ): Album {
        val songs = SongLoader.getSongs(
                SongLoader.makeSongCursor(
                        context,
                        AudioColumns.ALBUM_ID + "=?",
                        arrayOf(albumId.toString()),
                        getSongLoaderSortOrder(context)))
        val album = Album(songs)
        sortSongsByTrackNumber(album)
        return album
    }

    fun splitIntoAlbumsFlowable(
            songs: Observable<ArrayList<Song>>?
    ): Observable<ArrayList<Album>> {
        return Observable.create { e ->
            val albums = ArrayList<Album>()
            songs?.subscribe { songs1 ->
                for (song in songs1) {
                    getOrCreateAlbumFlowable(albums, song.albumId).subscribe { album ->
                        album.songs!!.add(song)
                    }
                }
            }
            for (album in albums) {
                sortSongsByTrackNumber(album)
            }
            e.onNext(albums)
            e.onComplete()
        }
    }

    fun getAllAlbums(
            context: Context
    ): ArrayList<Album> {
        val songs = SongLoader.getSongs(
                SongLoader.makeSongCursor(
                        context, null, null,
                        getSongLoaderSortOrder(context))
        )

        return splitIntoAlbums(songs)
    }

    fun splitIntoAlbums(
            songs: ArrayList<Song>?
    ): ArrayList<Album> {
        val albums = ArrayList<Album>()
        if (songs != null) {
            for (song in songs) {
                getOrCreateAlbum(albums, song.albumId).songs?.add(song)
            }
        }
        for (album in albums) {
            sortSongsByTrackNumber(album)
        }
        return albums
    }

    private fun getOrCreateAlbumFlowable(
            albums: ArrayList<Album>,
            albumId: Int
    ): Observable<Album> {
        return Observable.create { e ->
            for (album in albums) {
                if (!album.songs!!.isEmpty() && album.songs[0].albumId == albumId) {
                    e.onNext(album)
                    e.onComplete()
                    return@create
                }
            }
            val album = Album()
            albums.add(album)
            e.onNext(album)
            e.onComplete()
        }
    }

    private fun getOrCreateAlbum(
            albums: ArrayList<Album>,
            albumId: Int
    ): Album {
        for (album in albums) {
            if (album.songs!!.isNotEmpty() && album.songs[0].albumId == albumId) {
                return album
            }
        }
        val album = Album()
        albums.add(album)
        return album
    }

    private fun sortSongsByTrackNumber(
            album: Album
    ) {
        album.songs?.sortWith(Comparator { o1, o2 -> o1.trackNumber - o2.trackNumber })
    }

    private fun getSongLoaderSortOrder(context: Context): String {
        return PreferenceUtil.getInstance(context).albumSortOrder + ", " +
                PreferenceUtil.getInstance(context).albumDetailSongSortOrder
    }
}
