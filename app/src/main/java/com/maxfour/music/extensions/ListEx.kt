package com.maxfour.music.extensions

import com.maxfour.music.helper.MusicPlayerRemote
import com.maxfour.music.model.Song

fun ArrayList<Song>.lastElement(): Boolean {
    println("${this.size} ${this.indexOf(MusicPlayerRemote.currentSong)}")
    return this.size - 1 == this.indexOf(MusicPlayerRemote.currentSong)
}

fun ArrayList<Song>.fistElement(): Boolean {
    return 0 == this.indexOf(MusicPlayerRemote.currentSong)
}
