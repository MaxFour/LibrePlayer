package com.maxfour.music.helper.menu

import android.content.Intent
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.maxfour.music.R
import com.maxfour.music.activities.tageditor.AbsTagEditorActivity
import com.maxfour.music.activities.tageditor.SongTagEditorActivity
import com.maxfour.music.dialogs.AddToPlaylistDialog
import com.maxfour.music.dialogs.DeleteSongsDialog
import com.maxfour.music.dialogs.SongDetailDialog
import com.maxfour.music.helper.MusicPlayerRemote
import com.maxfour.music.interfaces.PaletteColorHolder
import com.maxfour.music.model.Song
import com.maxfour.music.util.MusicUtil
import com.maxfour.music.util.NavigationUtil
import com.maxfour.music.util.RingtoneManager

object SongMenuHelper {
    val MENU_RES = R.menu.menu_item_song

    fun handleMenuClick(activity: FragmentActivity, song: Song, menuItemId: Int): Boolean {
        when (menuItemId) {
            R.id.action_set_as_ringtone -> {
                if (RingtoneManager.requiresDialog(activity)) {
                    RingtoneManager.getDialog(activity)
                }
                val ringtoneManager = RingtoneManager(activity)
                ringtoneManager.setRingtone(song)
                return true
            }
            R.id.action_share -> {
                activity.startActivity(Intent.createChooser(MusicUtil.createShareSongFileIntent(song, activity), null))
                return true
            }
            R.id.action_delete_from_device -> {
                DeleteSongsDialog.create(song).show(activity.supportFragmentManager, "DELETE_SONGS")
                return true
            }
            R.id.action_add_to_playlist -> {
                AddToPlaylistDialog.create(song).show(activity.supportFragmentManager, "ADD_PLAYLIST")
                return true
            }
            R.id.action_play_next -> {
                MusicPlayerRemote.playNext(song)
                return true
            }
            R.id.action_add_to_current_playing -> {
                MusicPlayerRemote.enqueue(song)
                return true
            }
            R.id.action_tag_editor -> {
                val tagEditorIntent = Intent(activity, SongTagEditorActivity::class.java)
                tagEditorIntent.putExtra(AbsTagEditorActivity.EXTRA_ID, song.id)
                if (activity is PaletteColorHolder)
                    tagEditorIntent.putExtra(AbsTagEditorActivity.EXTRA_PALETTE, (activity as PaletteColorHolder).paletteColor)
                activity.startActivity(tagEditorIntent)
                return true
            }
            R.id.action_details -> {
                SongDetailDialog.create(song).show(activity.supportFragmentManager, "SONG_DETAILS")
                return true
            }
            R.id.action_go_to_album -> {
                NavigationUtil.goToAlbum(activity, song.albumId)
                return true
            }
            R.id.action_go_to_artist -> {
                NavigationUtil.goToArtist(activity, song.artistId)
                return true
            }
        }
        return false
    }

    abstract class OnClickSongMenu protected constructor(private val activity: AppCompatActivity) : View.OnClickListener, PopupMenu.OnMenuItemClickListener {

        open val menuRes: Int
            get() = MENU_RES

        abstract val song: Song

        override fun onClick(v: View) {
            val popupMenu = PopupMenu(activity, v)
            popupMenu.inflate(menuRes)
            popupMenu.setOnMenuItemClickListener(this)
            popupMenu.show()
        }

        override fun onMenuItemClick(item: MenuItem): Boolean {
            return handleMenuClick(activity, song, item.itemId)
        }
    }
}
