package com.maxfour.music.dialogs

import android.app.Dialog
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.DialogFragment
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.maxfour.music.R
import com.maxfour.music.model.PlaylistSong
import com.maxfour.music.util.PlaylistsUtil
import com.maxfour.music.util.PreferenceUtil

class RemoveFromPlaylistDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val songs = arguments!!.getParcelableArrayList<PlaylistSong>("songs")

        var title = 0
        var content: CharSequence = ""
        if (songs != null) {
            if (songs.size > 1) {
                title = R.string.remove_songs_from_playlist_title
                content = Html.fromHtml(getResources().getQuantityString(R.plurals.remove_x_songs_from_playlist, songs.size, songs.size))
            } else {
                title = R.string.remove_song_from_playlist_title
                content = Html.fromHtml(getString(com.maxfour.music.R.string.remove_song_x_from_playlist, songs[0].title))
            }
        }


        return MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT))
                .show {
                    title(title)
                    message(text = content)
                    negativeButton(android.R.string.cancel)
                    positiveButton(R.string.remove_action) {
                        if (activity == null)
                            return@positiveButton
                        PlaylistsUtil.removeFromPlaylist(activity!!, songs as MutableList<PlaylistSong>)
                    }
                    cornerRadius(PreferenceUtil.getInstance(requireContext()).dialogCorner)
                }

    }

    companion object {

        fun create(song: PlaylistSong): RemoveFromPlaylistDialog {
            val list = ArrayList<PlaylistSong>()
            list.add(song)
            return create(list)
        }

        fun create(songs: ArrayList<PlaylistSong>): RemoveFromPlaylistDialog {
            val dialog = RemoveFromPlaylistDialog()
            val args = Bundle()
            args.putParcelableArrayList("songs", songs)
            dialog.arguments = args
            return dialog
        }
    }
}