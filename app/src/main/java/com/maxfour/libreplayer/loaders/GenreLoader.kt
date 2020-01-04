package com.maxfour.libreplayer.loaders

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.BaseColumns
import android.provider.MediaStore.Audio.Genres
import com.maxfour.libreplayer.Constants.BASE_SELECTION
import com.maxfour.libreplayer.Constants.baseProjection
import com.maxfour.libreplayer.model.Genre
import com.maxfour.libreplayer.model.Song
import com.maxfour.libreplayer.util.PreferenceUtil

object GenreLoader {

    fun getAllGenres(context: Context): ArrayList<Genre> {
        return getGenresFromCursor(context, makeGenreCursor(context))
    }

    fun searchGenres(context: Context): ArrayList<Genre> {
        return getGenresFromCursorForSearch(context, makeGenreCursor(context));
    }

    fun getSongs(context: Context, genreId: Int): ArrayList<Song> {
        // The genres table only stores songs that have a genre specified,
        // so we need to get songs without a genre a different way.
        return if (genreId == -1) {
            getSongsWithNoGenre(context)
        } else SongLoader.getSongs(makeGenreSongCursor(context, genreId))

    }

    private fun getGenreFromCursor(context: Context, cursor: Cursor): Genre {
        val id = cursor.getInt(0)
        val name = cursor.getString(1)
        val songCount = getSongs(context, id).size
        return Genre(id, name, songCount)

    }

    private fun getGenreFromCursorWithOutSongs(context: Context, cursor: Cursor): Genre {
        val id = cursor.getInt(0)
        val name = cursor.getString(1)
        return Genre(id, name, -1)
    }

    private fun getSongsWithNoGenre(context: Context): ArrayList<Song> {
        val selection = BaseColumns._ID + " NOT IN " +
                "(SELECT " + Genres.Members.AUDIO_ID + " FROM audio_genres_map)"
        return SongLoader.getSongs(SongLoader.makeSongCursor(context, selection, null))
    }

    private fun hasSongsWithNoGenre(context: Context): Boolean {
        val allSongsCursor = SongLoader.makeSongCursor(context, null, null)
        val allSongsWithGenreCursor = makeAllSongsWithGenreCursor(context)

        if (allSongsCursor == null || allSongsWithGenreCursor == null) {
            return false
        }

        val hasSongsWithNoGenre = allSongsCursor.count > allSongsWithGenreCursor.count
        allSongsCursor.close()
        allSongsWithGenreCursor.close()
        return hasSongsWithNoGenre
    }

    private fun makeAllSongsWithGenreCursor(context: Context): Cursor? {
        try {
            return context.contentResolver.query(
                    Uri.parse("content://media/external/audio/genres/all/members"),
                    arrayOf(Genres.Members.AUDIO_ID), null, null, null)
        } catch (e: SecurityException) {
            return null
        }

    }

    private fun makeGenreSongCursor(context: Context, genreId: Int): Cursor? {
        try {
            return context.contentResolver.query(
                    Genres.Members.getContentUri("external", genreId.toLong()),
                    baseProjection, BASE_SELECTION, null, PreferenceUtil.getInstance(context).songSortOrder)
        } catch (e: SecurityException) {
            return null
        }

    }

    private fun getGenresFromCursor(context: Context, cursor: Cursor?): ArrayList<Genre> {
        val genres = arrayListOf<Genre>()
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val genre = getGenreFromCursor(context, cursor)
                    if (genre.songCount > 0) {
                        genres.add(genre)
                    } else {
                        // try to remove the empty genre from the media store
                        try {
                            context.contentResolver.delete(Genres.EXTERNAL_CONTENT_URI, Genres._ID + " == " + genre.id, null)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
        return genres
    }

    private fun getGenresFromCursorForSearch(context: Context, cursor: Cursor?): ArrayList<Genre> {
        val genres = arrayListOf<Genre>()
        if (cursor != null && cursor.moveToFirst()) {
            do {
                genres.add(getGenreFromCursorWithOutSongs(context, cursor))
            } while (cursor.moveToNext())
        }
        cursor?.close()
        return genres
    }


    private fun makeGenreCursor(context: Context): Cursor? {
        val projection = arrayOf(Genres._ID, Genres.NAME)
        try {
            return context.contentResolver.query(Genres.EXTERNAL_CONTENT_URI, projection, null, null, PreferenceUtil.getInstance(context).genreSortOrder)
        } catch (e: SecurityException) {
            return null
        }
    }
}
