package com.maxfour.libreplayer.dialogs

import android.app.Dialog
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.DialogFragment
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.maxfour.libreplayer.R
import com.maxfour.libreplayer.model.smartplaylist.AbsSmartPlaylist
import com.maxfour.libreplayer.util.PreferenceUtil

class ClearSmartPlaylistDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val playlist = arguments!!.getParcelable<AbsSmartPlaylist>("playlist")
        val title = R.string.clear_playlist_title

        val content = Html.fromHtml(getString(R.string.clear_playlist_x, playlist!!.name))

        return MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            title(title)
            cornerRadius(PreferenceUtil.getInstance(requireContext()).dialogCorner)
            message(text = content)
            positiveButton(R.string.clear_action) {
                if (activity == null) {
                    return@positiveButton
                }
                playlist.clear(activity!!)
            }
            negativeButton { (android.R.string.cancel) }
        }
    }

    companion object {

        fun create(playlist: AbsSmartPlaylist): ClearSmartPlaylistDialog {
            val dialog = ClearSmartPlaylistDialog()
            val args = Bundle()
            args.putParcelable("playlist", playlist)
            dialog.arguments = args
            return dialog
        }
    }
}
