package com.maxfour.libreplayer.loaders

import android.content.Context
import android.provider.MediaStore.Audio.AudioColumns
import com.maxfour.libreplayer.model.Album
import com.maxfour.libreplayer.model.Song
import com.maxfour.libreplayer.util.PreferenceUtil
import java.util.*
import kotlin.collections.ArrayList

object AlbumLoader {

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

    fun getAllAlbums(
            context: Context
    ): ArrayList<Album> {
        val songs = SongLoader.getSongs(SongLoader.makeSongCursor(context, null, null, getSongLoaderSortOrder(context)))
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

    private fun sortSongsByTrackNumber(album: Album) {
        album.songs?.sortWith(Comparator { o1, o2 -> o1.songNumber.compareTo(o2.songNumber) })
    }

    private fun getSongLoaderSortOrder(context: Context): String {
        return PreferenceUtil.getInstance(context).albumSortOrder + ", " + PreferenceUtil.getInstance(context).albumSongSortOrder
    }
}
