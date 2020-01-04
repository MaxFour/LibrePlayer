package com.maxfour.libreplayer.interfaces

interface MusicServiceEventListener {
    fun onServiceConnected()

    fun onServiceDisconnected()

    fun onQueueChanged()

    fun onPlayingMetaChanged()

    fun onPlayStateChanged()

    fun onRepeatModeChanged()

    fun onShuffleModeChanged()

    fun onMediaStoreChanged()
}
