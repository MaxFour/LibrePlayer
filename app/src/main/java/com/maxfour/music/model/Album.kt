package com.maxfour.music.model

import java.util.*


class Album {
    val songs: ArrayList<Song>?

    val id: Int
        get() = safeGetFirstSong().albumId

    val title: String?
        get() = safeGetFirstSong().albumName

    val artistId: Int
        get() = safeGetFirstSong().artistId

    val artistName: String?
        get() = safeGetFirstSong().artistName

    val year: Int
        get() = safeGetFirstSong().year

    val dateModified: Long
        get() = safeGetFirstSong().dateModified

    val songCount: Int
        get() = songs!!.size

    constructor(songs: ArrayList<Song>) {
        this.songs = songs
    }

    constructor() {
        this.songs = ArrayList()
    }

    fun safeGetFirstSong(): Song {
        return if (songs!!.isEmpty()) Song.emptySong else songs[0]
    }
}
