package com.maxfour.music.dialogs

import android.app.Dialog
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.DialogFragment
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.maxfour.music.R
import com.maxfour.music.R.string
import com.maxfour.music.model.Playlist
import com.maxfour.music.util.PlaylistsUtil
import com.maxfour.music.util.PreferenceUtil
import java.util.*

class DeletePlaylistDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val playlists = arguments!!.getParcelableArrayList<Playlist>("playlist")
        val title: Int
        val content: CharSequence
        //noinspection ConstantConditions
        if (playlists!!.size > 1) {
            title = string.delete_playlists_title
            content = Html.fromHtml(getResources().getQuantityString(R.plurals.delete_x_playlists, playlists.size, playlists.size))
        } else {
            title = string.delete_playlist_title
            content = Html.fromHtml(getString(string.delete_playlist_x, playlists[0].name))
        }

        return MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT))
                .show {
                    cornerRadius(PreferenceUtil.getInstance(requireContext()).dialogCorner)
                    title(title)
                    message(text = content)
                    negativeButton(android.R.string.cancel)
                    positiveButton(R.string.action_delete) {
                        if (activity == null)
                            return@positiveButton
                        PlaylistsUtil.deletePlaylists(activity!!, playlists)
                    }
                    negativeButton(android.R.string.cancel)
                }
    }

    companion object {

        fun create(playlist: Playlist): DeletePlaylistDialog {
            val list = ArrayList<Playlist>()
            list.add(playlist)
            return create(list)
        }

        fun create(playlist: ArrayList<Playlist>): DeletePlaylistDialog {
            val dialog = DeletePlaylistDialog()
            val args = Bundle()
            args.putParcelableArrayList("playlist", playlist)
            dialog.arguments = args
            return dialog
        }
    }

}