package com.maxfour.libreplayer.loaders

import android.content.Context
import com.maxfour.libreplayer.R
import com.maxfour.libreplayer.model.Genre
import java.util.*

object SearchLoader {
    fun searchAll(context: Context, query: String?): MutableList<Any> {
        val results = mutableListOf<Any>()
        query?.let { searchString ->
            val songs = SongLoader.getSongs(context, searchString)
            if (songs.isNotEmpty()) {
                results.add(context.resources.getString(R.string.songs))
                results.addAll(songs)
            }

            val artists = ArtistLoader.getArtists(context, searchString)
            if (artists.isNotEmpty()) {
                results.add(context.resources.getString(R.string.artists))
                results.addAll(artists)
            }

            val albums = AlbumLoader.getAlbums(context, searchString)
            if (albums.isNotEmpty()) {
                results.add(context.resources.getString(R.string.albums))
                results.addAll(albums)
            }
            val genres: List<Genre> = GenreLoader.searchGenres(context).filter { genre -> genre.name.toLowerCase(Locale.getDefault()).contains(searchString.toLowerCase(Locale.getDefault())) }
            if (genres.isNotEmpty()) {
                results.add(context.resources.getString(R.string.genres))
                results.addAll(genres)
            }
        }
        return results
    }
}
