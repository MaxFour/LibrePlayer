package com.maxfour.libreplayer.loaders

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import com.maxfour.libreplayer.model.Album
import com.maxfour.libreplayer.model.Artist
import com.maxfour.libreplayer.model.Song
import com.maxfour.libreplayer.util.PreferenceUtil

object LastAddedSongsLoader {

    fun getLastAddedSongs(context: Context): ArrayList<Song> {
        return SongLoader.getSongs(makeLastAddedCursor(context))
    }

    private fun makeLastAddedCursor(context: Context): Cursor? {
        val cutoff = PreferenceUtil.getInstance(context).lastAddedCutoff

        return SongLoader.makeSongCursor(
                context,
                MediaStore.Audio.Media.DATE_ADDED + ">?",
                arrayOf(cutoff.toString()),
                MediaStore.Audio.Media.DATE_ADDED + " DESC")
    }

    fun getLastAddedAlbums(context: Context): ArrayList<Album> {
        return AlbumLoader.splitIntoAlbums(getLastAddedSongs(context))
    }

    fun getLastAddedArtists(context: Context): ArrayList<Artist> {
        return ArtistLoader.splitIntoArtists(getLastAddedAlbums(context))
    }
}
