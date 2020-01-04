package com.maxfour.libreplayer.service

import android.os.Handler

import com.maxfour.libreplayer.service.MusicService.PLAY_STATE_CHANGED

class ThrottledSeekHandler(
        private val musicService: MusicService,
        private val handler: Handler
) : Runnable {

    fun notifySeek() {
        musicService.updateMediaSessionPlaybackState()
        musicService.updateMediaSessionMetaData()
        handler.removeCallbacks(this)
        handler.postDelayed(this, THROTTLE)
    }

    override fun run() {
        musicService.savePositionInTrack()
        musicService.sendPublicIntent(PLAY_STATE_CHANGED) // for musixmatch synced lyrics
    }

    companion object {
        // milliseconds to throttle before calling run() to aggregate events
        private val THROTTLE: Long = 500
    }
}
