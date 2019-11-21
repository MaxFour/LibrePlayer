package com.maxfour.music.dialogs

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.list.listItems
import com.maxfour.music.R
import com.maxfour.music.model.Song
import com.maxfour.music.util.MusicUtil
import com.maxfour.music.util.PreferenceUtil

class SongShareDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val song: Song? = arguments!!.getParcelable("song")
        val currentlyListening: String = getString(R.string.currently_listening_to_x_by_x, song?.title, song?.artistName)

        return MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT))
                .title(R.string.what_do_you_want_to_share)
                .show {
                    cornerRadius(PreferenceUtil.getInstance(requireContext()).dialogCorner)
                    listItems(items = listOf(getString(com.maxfour.music.R.string.the_audio_file), "\u201C" + currentlyListening + "\u201D")) { dialog, index, text ->
                        when (index) {
                            0 -> {
                                startActivity(Intent.createChooser(song?.let { MusicUtil.createShareSongFileIntent(it, context) }, null))
                            }
                            1 -> {
                                activity!!.startActivity(
                                        Intent.createChooser(
                                                Intent()
                                                        .setAction(Intent.ACTION_SEND)
                                                        .putExtra(Intent.EXTRA_TEXT, currentlyListening)
                                                        .setType("text/plain"),
                                                null
                                        )
                                )
                            }
                        }
                    }
                }
    }

    companion object {

        fun create(song: Song): SongShareDialog {
            val dialog = SongShareDialog()
            val args = Bundle()
            args.putParcelable("song", song)
            dialog.arguments = args
            return dialog
        }
    }
}
