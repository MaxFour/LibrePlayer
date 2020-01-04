package com.maxfour.libreplayer.loaders

import android.content.Context
import android.provider.MediaStore.Audio.AudioColumns
import com.maxfour.libreplayer.model.Album
import com.maxfour.libreplayer.model.Artist
import com.maxfour.libreplayer.util.PreferenceUtil

object ArtistLoader {
    private fun getSongLoaderSortOrder(context: Context): String {
        return PreferenceUtil.getInstance(context).artistSortOrder + ", " + PreferenceUtil.getInstance(context).artistAlbumSortOrder + ", " + PreferenceUtil.getInstance(context).albumSongSortOrder
    }

    fun getAllArtists(context: Context): ArrayList<Artist> {
        val songs = SongLoader.getSongs(SongLoader.makeSongCursor(
                context,
                null, null,
                getSongLoaderSortOrder(context))
        )
        return splitIntoArtists(AlbumLoader.splitIntoAlbums(songs))
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
