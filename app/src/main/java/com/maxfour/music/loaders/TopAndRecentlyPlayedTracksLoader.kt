package com.maxfour.music.loaders

import android.content.Context
import android.database.Cursor
import android.provider.BaseColumns
import com.maxfour.music.Constants.NUMBER_OF_TOP_TRACKS
import com.maxfour.music.model.Album
import com.maxfour.music.model.Artist
import com.maxfour.music.model.Song
import com.maxfour.music.providers.HistoryStore
import com.maxfour.music.providers.SongPlayCountStore
import io.reactivex.Observable
import java.util.*

object TopAndRecentlyPlayedTracksLoader {

    fun getRecentlyPlayedTracksFlowable(context: Context): Observable<ArrayList<Song>> {
        return SongLoader.getSongsFlowable(makeRecentTracksCursorAndClearUpDatabase(context))
    }

    fun getRecentlyPlayedTracks(context: Context): ArrayList<Song> {
        return SongLoader.getSongs(makeRecentTracksCursorAndClearUpDatabase(context))
    }

    fun getTopTracksFlowable(context: Context): Observable<ArrayList<Song>> {
        return SongLoader.getSongsFlowable(makeTopTracksCursorAndClearUpDatabase(context))
    }

    fun getTopTracks(context: Context): ArrayList<Song> {
        return SongLoader.getSongs(makeTopTracksCursorAndClearUpDatabase(context))
    }

    private fun makeRecentTracksCursorAndClearUpDatabase(context: Context): Cursor? {
        val retCursor = makeRecentTracksCursorImpl(context)

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

    private fun makeTopTracksCursorAndClearUpDatabase(context: Context): Cursor? {
        val retCursor = makeTopTracksCursorImpl(context)

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

    private fun makeRecentTracksCursorImpl(context: Context): SortedLongCursor? {
        // first get the top results ids from the internal database
        val songs = HistoryStore.getInstance(context).queryRecentIds()

        try {
            return makeSortedCursor(context, songs,
                    songs!!.getColumnIndex(HistoryStore.RecentStoreColumns.ID))
        } finally {
            songs?.close()
        }
    }

    private fun makeTopTracksCursorImpl(context: Context): SortedLongCursor? {
        // first get the top results ids from the internal database
        val songs = SongPlayCountStore.getInstance(context)
                .getTopPlayedResults(NUMBER_OF_TOP_TRACKS)

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

            // this tracks the order of the ids
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
                // now return the wrapped TopTracksCursor to handle sorting given order
                return SortedLongCursor(songCursor, order, BaseColumns._ID)
            }
        }

        return null
    }

    fun getTopAlbumsFlowable(
            context: Context
    ): Observable<ArrayList<Album>> {
        return Observable.create { e ->
            getTopTracksFlowable(context).subscribe { songs ->
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
        return AlbumLoader.splitIntoAlbums(getTopTracks(context))
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
