package com.maxfour.libreplayer.dialogs

import android.app.Dialog
import android.os.Bundle
import android.provider.MediaStore.Audio.Playlists.Members.PLAYLIST_ID
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.maxfour.appthemehelper.util.MaterialUtil
import com.maxfour.libreplayer.R
import com.maxfour.libreplayer.R.layout
import com.maxfour.libreplayer.R.string
import com.maxfour.libreplayer.extensions.appHandleColor
import com.maxfour.libreplayer.util.PlaylistsUtil
import com.maxfour.libreplayer.util.PreferenceUtil

class RenamePlaylistDialog : DialogFragment() {
    private lateinit var playlistView: TextInputEditText
    private lateinit var actionNewPlaylistContainer: TextInputLayout

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val materialDialog = MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT))
                .show {
                    cornerRadius(PreferenceUtil.getInstance(requireContext()).dialogCorner)
                    title(string.rename_playlist_title)
                    customView(layout.dialog_playlist)
                    negativeButton(android.R.string.cancel)
                    positiveButton(string.action_rename) {
                        if (playlistView.toString().trim { it <= ' ' } != "") {
                            val playlistId = arguments!!.getLong(PLAYLIST_ID)
                            PlaylistsUtil.renamePlaylist(context, playlistId, playlistView.text!!.toString())
                        }
                    }
                }

        val dialogView = materialDialog.getCustomView()
        playlistView = dialogView.findViewById(R.id.actionNewPlaylist)
        actionNewPlaylistContainer = dialogView.findViewById(R.id.actionNewPlaylistContainer)

        MaterialUtil.setTint(actionNewPlaylistContainer, false)

        val playlistId = arguments!!.getLong(PLAYLIST_ID)
        playlistView.appHandleColor().setText(PlaylistsUtil.getNameForPlaylist(context!!, playlistId), TextView.BufferType.EDITABLE)
        return materialDialog
    }

    companion object {

        fun create(playlistId: Long): RenamePlaylistDialog {
            val dialog = RenamePlaylistDialog()
            val args = Bundle()
            args.putLong(PLAYLIST_ID, playlistId)
            dialog.arguments = args
            return dialog
        }
    }
}
