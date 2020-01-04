package com.maxfour.libreplayer.loaders

import android.content.Context
import android.database.Cursor
import android.provider.BaseColumns
import com.maxfour.libreplayer.Constants.NUMBER_OF_TOP_SONGS
import com.maxfour.libreplayer.model.Album
import com.maxfour.libreplayer.model.Artist
import com.maxfour.libreplayer.model.Song
import com.maxfour.libreplayer.providers.HistoryStore
import com.maxfour.libreplayer.providers.SongPlayCountStore
import io.reactivex.Observable
import java.util.*

object TopAndRecentlyPlayedSongsLoader {

    fun getRecentlyPlayedSongsFlowable(context: Context): Observable<ArrayList<Song>> {
        return SongLoader.getSongsFlowable(makeRecentSongsCursorAndClearUpDatabase(context))
    }

    fun getRecentlyPlayedSongs(context: Context): ArrayList<Song> {
        return SongLoader.getSongs(makeRecentSongsCursorAndClearUpDatabase(context))
    }

    fun getTopSongsFlowable(context: Context): Observable<ArrayList<Song>> {
        return SongLoader.getSongsFlowable(makeTopSongsCursorAndClearUpDatabase(context))
    }

    fun getTopSongs(context: Context): ArrayList<Song> {
        return SongLoader.getSongs(makeTopSongsCursorAndClearUpDatabase(context))
    }

    private fun makeRecentSongsCursorAndClearUpDatabase(context: Context): Cursor? {
        val retCursor = makeRecentSongsCursorImpl(context)

        // clean up the databases with any ids not found
        if (retCursor != null) {
            val missingIds = retCursor.missingIds
            if (missingIds != null && missingIds.size > 0) {
                for (id in missingIds) {
                    HistoryStore.getInstance(context).removeSongId(id)
                }
            }
        }
        return retCursor
    }

    private fun makeTopSongsCursorAndClearUpDatabase(context: Context): Cursor? {
        val retCursor = makeTopSongsCursorImpl(context)

        // clean up the databases with any ids not found
        if (retCursor != null) {
            val missingIds = retCursor.missingIds
            if (missingIds != null && missingIds.size > 0) {
                for (id in missingIds) {
                    SongPlayCountStore.getInstance(context).removeItem(id)
                }
            }
        }
        return retCursor
    }

    private fun makeRecentSongsCursorImpl(context: Context): SortedLongCursor? {
        // first get the top results ids from the internal database
        val songs = HistoryStore.getInstance(context).queryRecentIds()

        try {
            return makeSortedCursor(context, songs,
                    songs!!.getColumnIndex(HistoryStore.RecentStoreColumns.ID))
        } finally {
            songs?.close()
        }
    }

    private fun makeTopSongsCursorImpl(context: Context): SortedLongCursor? {
        // first get the top results ids from the internal database
        val songs = SongPlayCountStore.getInstance(context)
                .getTopPlayedResults(NUMBER_OF_TOP_SONGS)

        songs.use { localSongs ->
            return makeSortedCursor(context, localSongs,
                    localSongs.getColumnIndex(SongPlayCountStore.SongPlayCountColumns.ID))
        }
    }

    private fun makeSortedCursor(context: Context,
                                 cursor: Cursor?, idColumn: Int): SortedLongCursor? {

        if (cursor != null && cursor.moveToFirst()) {
            // create the list of ids to select against
            val selection = StringBuilder()
            selection.append(BaseColumns._ID)
            selection.append(" IN (")

            // this songs the order of the ids
            val order = LongArray(cursor.count)

            var id = cursor.getLong(idColumn)
            selection.append(id)
            order[cursor.position] = id

            while (cursor.moveToNext()) {
                selection.append(",")

                id = cursor.getLong(idColumn)
                order[cursor.position] = id
                selection.append(id.toString())
            }

            selection.append(")")

            // get a list of songs with the data given the selection statement
            val songCursor = SongLoader.makeSongCursor(context, selection.toString(), null)
            if (songCursor != null) {
                // now return the wrapped TopSongsCursor to handle sorting given order
                return SortedLongCursor(songCursor, order, BaseColumns._ID)
            }
        }

        return null
    }

    fun getTopAlbumsFlowable(
            context: Context
    ): Observable<ArrayList<Album>> {
        return Observable.create { e ->
            getTopSongsFlowable(context).subscribe { songs ->
                if (songs.size > 0) {
                    e.onNext(AlbumLoader.splitIntoAlbums(songs))
                }
                e.onComplete()
            }
        }
    }

    fun getTopAlbums(
            context: Context
    ): ArrayList<Album> {
        arrayListOf<Album>()
        return AlbumLoader.splitIntoAlbums(getTopSongs(context))
    }

    fun getTopArtistsFlowable(context: Context): Observable<ArrayList<Artist>> {
        return Observable.create { e ->
            getTopAlbumsFlowable(context).subscribe { albums ->
                if (albums.size > 0) {
                    e.onNext(ArtistLoader.splitIntoArtists(albums))
                }
                e.onComplete()
            }
        }
    }

    fun getTopArtists(context: Context): ArrayList<Artist> {
        return ArtistLoader.splitIntoArtists(getTopAlbums(context))
    }
}
