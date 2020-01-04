package com.maxfour.libreplayer.helper.menu

import androidx.fragment.app.FragmentActivity

import com.maxfour.libreplayer.R
import com.maxfour.libreplayer.dialogs.AddToPlaylistDialog
import com.maxfour.libreplayer.dialogs.DeleteSongsDialog
import com.maxfour.libreplayer.helper.MusicPlayerRemote
import com.maxfour.libreplayer.model.Song
import java.util.*

object SongsMenuHelper {
    fun handleMenuClick(activity: FragmentActivity, songs: ArrayList<Song>, menuItemId: Int): Boolean {
        when (menuItemId) {
            R.id.action_play_next -> {
                MusicPlayerRemote.playNext(songs)
                return true
            }
            R.id.action_add_to_current_playing -> {
                MusicPlayerRemote.enqueue(songs)
                return true
            }
            R.id.action_add_to_playlist -> {
                AddToPlaylistDialog.create(songs).show(activity.supportFragmentManager, "ADD_PLAYLIST")
                return true
            }
            R.id.action_delete_from_device -> {
                DeleteSongsDialog.create(songs).show(activity.supportFragmentManager, "DELETE_SONGS")
                return true
            }
        }
        return false
    }
}
