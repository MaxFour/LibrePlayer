package com.maxfour.music.misc

import android.content.Context
import android.text.TextUtils
import com.maxfour.music.R
import com.maxfour.music.loaders.AlbumLoader
import com.maxfour.music.loaders.ArtistLoader
import com.maxfour.music.loaders.SongLoader
import java.util.*

internal class AsyncSearchResultLoader(context: Context, private val query: String) : WrappedAsyncTaskLoader<List<Any>>(context) {

    override fun loadInBackground(): List<Any>? {
        val results = ArrayList<Any>()
        if (!TextUtils.isEmpty(query)) {
            val songs = SongLoader.getSongs(context, query.trim { it <= ' ' })
            if (!songs.isEmpty()) {
                results.add(context.resources.getString(R.string.songs))
                results.addAll(songs)
            }

            val artists = ArtistLoader.getArtists(context, query.trim { it <= ' ' })
            if (!artists.isEmpty()) {
                results.add(context.resources.getString(R.string.artists))
                results.addAll(artists)
            }

            val albums = AlbumLoader.getAlbums(context, query.trim { it <= ' ' })
            if (!albums.isEmpty()) {
                results.add(context.resources.getString(R.string.albums))
                results.addAll(albums)
            }
        }
        return results
    }
}