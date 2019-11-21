package com.maxfour.music.dialogs

import android.app.Dialog
import android.os.Bundle
import android.provider.MediaStore
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
import com.maxfour.music.R
import com.maxfour.music.R.layout
import com.maxfour.music.R.string
import com.maxfour.music.extensions.appHandleColor
import com.maxfour.music.model.Song
import com.maxfour.music.util.PlaylistsUtil
import com.maxfour.music.util.PreferenceUtil

class CreatePlaylistDialog : DialogFragment() {

    private lateinit var playlistView: TextInputEditText
    private lateinit var actionNewPlaylistContainer: TextInputLayout

    override fun onCreateDialog(
            savedInstanceState: Bundle?
    ): Dialog {
        val materialDialog = MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT))
                .show {
                    cornerRadius(PreferenceUtil.getInstance(requireContext()).dialogCorner)
                    title(string.new_playlist_title)
                    customView(layout.dialog_playlist)
                    negativeButton(android.R.string.cancel)
                    positiveButton(string.create_action) {
                        if (activity == null) {
                            return@positiveButton
                        }
                        val songs = arguments?.getParcelableArrayList<Song>("songs")
                                ?: return@positiveButton

                        if (playlistView.text.toString().trim { it <= ' ' }.isNotEmpty()) {
                            val playlistId = PlaylistsUtil.createPlaylist(requireContext(), playlistView.text.toString())
                            if (playlistId != -1 && activity != null) {
                                PlaylistsUtil.addToPlaylist(requireContext(), songs, playlistId, true)
                            }
                        }
                    }
                }

        val dialogView = materialDialog.getCustomView()
        playlistView = dialogView.findViewById(R.id.actionNewPlaylist)
        actionNewPlaylistContainer = dialogView.findViewById(R.id.actionNewPlaylistContainer)

        MaterialUtil.setTint(actionNewPlaylistContainer, false)

        val playlistId = arguments!!.getLong(MediaStore.Audio.Playlists.Members.PLAYLIST_ID)
        playlistView.appHandleColor().setText(PlaylistsUtil.getNameForPlaylist(requireContext(), playlistId), TextView.BufferType.EDITABLE)
        return materialDialog
    }

    companion object {
        @JvmOverloads
        fun create(song: Song? = null): CreatePlaylistDialog {
            val list = ArrayList<Song>()
            if (song != null) {
                list.add(song)
            }
            return create(list)
        }

        fun create(songs: ArrayList<Song>): CreatePlaylistDialog {
            val dialog = CreatePlaylistDialog()
            val args = Bundle()
            args.putParcelableArrayList("songs", songs)
            dialog.arguments = args
            return dialog
        }
    }
}