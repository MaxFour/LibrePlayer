package com.maxfour.music.loaders

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import android.provider.MediaStore.Audio.AudioColumns
import com.maxfour.music.Constants.BASE_SELECTION
import com.maxfour.music.model.AbsCustomPlaylist
import com.maxfour.music.model.Playlist
import com.maxfour.music.model.PlaylistSong
import com.maxfour.music.model.Song
import io.reactivex.Observable
import java.util.*

object PlaylistSongsLoader {

    fun getPlaylistSongListFlowable(
            context: Context,
            playlist: Playlist
    ): Observable<ArrayList<Song>> {
        return (playlist as? AbsCustomPlaylist)?.getSongsFlowable(context)
                ?: getPlaylistSongListFlowable(context, playlist.id)
    }

    fun getPlaylistSongList(
            context: Context,
            playlist: Playlist
    ): ArrayList<Song> {
        return (playlist as? AbsCustomPlaylist)?.getSongs(context)
                ?: getPlaylistSongList(context, playlist.id)
    }


    fun getPlaylistSongListFlowable(context: Context, playlistId: Int): Observable<ArrayList<Song>> {
        return Observable.create { e ->
            val songs = ArrayList<Song>()
            val cursor = makePlaylistSongCursor(context, playlistId)

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    songs.add(getPlaylistSongFromCursorImpl(cursor, playlistId))
                } while (cursor.moveToNext())
            }
            cursor?.close()
            e.onNext(songs)
            e.onComplete()
        }
    }

    fun getPlaylistSongList(context: Context, playlistId: Int): ArrayList<Song> {
        val songs = arrayListOf<Song>()
        val cursor = makePlaylistSongCursor(context, playlistId)

        if (cursor != null && cursor.moveToFirst()) {
            do {
                songs.add(getPlaylistSongFromCursorImpl(cursor, playlistId))
            } while (cursor.moveToNext())
        }
        cursor?.close()
        return songs
    }


    private fun getPlaylistSongFromCursorImpl(cursor: Cursor, playlistId: Int): PlaylistSong {
        val id = cursor.getInt(0)
        val title = cursor.getString(1)
        val trackNumber = cursor.getInt(2)
        val year = cursor.getInt(3)
        val duration = cursor.getLong(4)
        val data = cursor.getString(5)
        val dateModified = cursor.getLong(6)
        val albumId = cursor.getInt(7)
        val albumName = cursor.getString(8)
        val artistId = cursor.getInt(9)
        val artistName = cursor.getString(10)
        val idInPlaylist = cursor.getInt(11)
        val composer = cursor.getString(12)

        return PlaylistSong(id, title, trackNumber, year, duration, data, dateModified, albumId, albumName, artistId, artistName, playlistId, idInPlaylist, composer)
    }

    private fun makePlaylistSongCursor(context: Context, playlistId: Int): Cursor? {
        try {
            return context.contentResolver.query(
                    MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId.toLong()),
                    arrayOf(MediaStore.Audio.Playlists.Members.AUDIO_ID, // 0
                            AudioColumns.TITLE, // 1
                            AudioColumns.TRACK, // 2
                            AudioColumns.YEAR, // 3
                            AudioColumns.DURATION, // 4
                            AudioColumns.DATA, // 5
                            AudioColumns.DATE_MODIFIED, // 6
                            AudioColumns.ALBUM_ID, // 7
                            AudioColumns.ALBUM, // 8
                            AudioColumns.ARTIST_ID, // 9
                            AudioColumns.ARTIST, // 10
                            MediaStore.Audio.Playlists.Members._ID,//11
                            AudioColumns.COMPOSER)// 12
                    , BASE_SELECTION, null,
                    MediaStore.Audio.Playlists.Members.DEFAULT_SORT_ORDER)
        } catch (e: SecurityException) {
            return null
        }
    }
}
