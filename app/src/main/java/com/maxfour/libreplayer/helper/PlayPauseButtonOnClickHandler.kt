package com.maxfour.libreplayer.helper

import android.view.View

class PlayPauseButtonOnClickHandler : View.OnClickListener {
    override fun onClick(v: View) {
        if (MusicPlayerRemote.isPlaying) {
            MusicPlayerRemote.pauseSong()
        } else {
            MusicPlayerRemote.resumePlaying()
        }
    }
}
