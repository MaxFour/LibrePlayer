package com.maxfour.music.loaders

import android.content.Context
import com.maxfour.music.R

object SearchLoader {
    fun searchAll(context: Context, query: String?): MutableList<Any> {
        val results = mutableListOf<Any>()
        query?.let {
            val songs = SongLoader.getSongs(context, it)
            if (songs.isNotEmpty()) {
                results.add(context.resources.getString(R.string.songs))
                results.addAll(songs)
            }

            val artists = ArtistLoader.getArtists(context, it)
            if (artists.isNotEmpty()) {
                results.add(context.resources.getString(R.string.artists))
                results.addAll(artists)
            }

            val albums = AlbumLoader.getAlbums(context, it)
            if (albums.isNotEmpty()) {
                results.add(context.resources.getString(R.string.albums))
                results.addAll(albums)
            }
        }
        return results

    }
}
