package com.maxfour.music.loaders

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import com.maxfour.music.model.Album
import com.maxfour.music.model.Artist
import com.maxfour.music.model.Song
import com.maxfour.music.util.PreferenceUtil
import io.reactivex.Observable

object LastAddedSongsLoader {


    fun getLastAddedSongsFlowable(context: Context): Observable<ArrayList<Song>> {
        return SongLoader.getSongsFlowable(makeLastAddedCursor(context))
    }

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


    fun getLastAddedAlbumsFlowable(context: Context): Observable<ArrayList<Album>> {
        return AlbumLoader.splitIntoAlbumsFlowable(getLastAddedSongsFlowable(context))
    }


    fun getLastAddedAlbums(context: Context): ArrayList<Album> {
        return AlbumLoader.splitIntoAlbums(getLastAddedSongs(context))
    }


    fun getLastAddedArtistsFlowable(context: Context): Observable<ArrayList<Artist>> {
        return ArtistLoader.splitIntoArtists(getLastAddedAlbumsFlowable(context))
    }

    fun getLastAddedArtists(context: Context): ArrayList<Artist> {
        return ArtistLoader.splitIntoArtists(getLastAddedAlbums(context))
    }
}
