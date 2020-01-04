package com.maxfour.libreplayer.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.list.listItems
import com.maxfour.libreplayer.R
import com.maxfour.libreplayer.loaders.PlaylistLoader
import com.maxfour.libreplayer.model.Song
import com.maxfour.libreplayer.util.PlaylistsUtil
import com.maxfour.libreplayer.util.PreferenceUtil

class AddToPlaylistDialog : DialogFragment() {

    override fun onCreateDialog(
            savedInstanceState: Bundle?
    ): Dialog {
        val playlists = PlaylistLoader.getAllPlaylists(requireContext())
        val playlistNames: MutableList<String> = mutableListOf()
        playlistNames.add(requireContext().resources.getString(R.string.action_new_playlist))
        for (p in playlists) {
            playlistNames.add(p.name)
        }

        return MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            title(R.string.add_playlist_title)
            cornerRadius(PreferenceUtil.getInstance(requireContext()).dialogCorner)
            listItems(items = playlistNames) { dialog, index, _ ->
                val songs = arguments!!.getParcelableArrayList<Song>("songs") ?: return@listItems
                if (index == 0) {
                    dialog.dismiss()
                    activity?.supportFragmentManager?.let { CreatePlaylistDialog.create(songs).show(it, "ADD_TO_PLAYLIST") }
                } else {
                    dialog.dismiss()
                    PlaylistsUtil.addToPlaylist(requireContext(), songs, playlists[index - 1].id, true)
                }
            }
        }
    }

    companion object {

        fun create(song: Song): AddToPlaylistDialog {
            val list = ArrayList<Song>()
            list.add(song)
            return create(list)
        }

        fun create(songs: ArrayList<Song>): AddToPlaylistDialog {
            val dialog = AddToPlaylistDialog()
            val args = Bundle()
            args.putParcelableArrayList("songs", songs)
            dialog.arguments = args
            return dialog
        }
    }
}
