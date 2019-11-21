package com.maxfour.music.dialogs

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.DialogFragment
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.maxfour.music.R
import com.maxfour.music.activities.saf.SAFGuideActivity
import com.maxfour.music.helper.MusicPlayerRemote
import com.maxfour.music.model.Song
import com.maxfour.music.util.MusicUtil
import com.maxfour.music.util.PreferenceUtil
import com.maxfour.music.util.SAFUtil

class DeleteSongsDialog : DialogFragment() {
    @JvmField
    var currentSong: Song? = null
    @JvmField
    var songsToRemove: List<Song>? = null

    private var deleteSongsAsyncTask: DeleteSongsAsyncTask? = null


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val songs: ArrayList<Song>? = arguments?.getParcelableArrayList("songs")
        var title = 0
        var content: CharSequence = ""
        if (songs != null) {
            if (songs.size > 1) {
                title = R.string.delete_songs_title
                content = Html.fromHtml(getResources().getQuantityString(R.plurals.delete_x_songs, songs.size, songs.size))
            } else {
                title = R.string.delete_song_title
                content = Html.fromHtml(getString(R.string.delete_song_x, songs[0].title))
            }
        }

        return MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            title(title)
            message(text = content)
            negativeButton(android.R.string.cancel) {
                dismiss()
            }
            cornerRadius(PreferenceUtil.getInstance(requireContext()).dialogCorner)
            noAutoDismiss()
            positiveButton(R.string.action_delete) {
                if (songs != null) {
                    if ((songs.size == 1) && MusicPlayerRemote.isPlaying(songs[0])) {
                        MusicPlayerRemote.playNextSong()
                    }
                }

                songsToRemove = songs
                deleteSongsAsyncTask = DeleteSongsAsyncTask(this@DeleteSongsDialog)
                deleteSongsAsyncTask?.execute(DeleteSongsAsyncTask.LoadingInfo(songs, null))
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SAFGuideActivity.REQUEST_CODE_SAF_GUIDE -> {
                SAFUtil.openTreePicker(this)
            }
            SAFUtil.REQUEST_SAF_PICK_TREE,
            SAFUtil.REQUEST_SAF_PICK_FILE -> {
                if (deleteSongsAsyncTask != null) {
                    deleteSongsAsyncTask?.cancel(true)
                }
                deleteSongsAsyncTask = DeleteSongsAsyncTask(this)
                deleteSongsAsyncTask?.execute(DeleteSongsAsyncTask.LoadingInfo(requestCode, resultCode, data))
            }
        }
    }

    fun deleteSongs(songs: List<Song>, safUris: List<Uri>?) {
        MusicUtil.deleteTracks(activity!!, songs, safUris) { this.dismiss() }
    }

    companion object {

        fun create(song: Song): DeleteSongsDialog {
            val list = ArrayList<Song>()
            list.add(song)
            return create(list)
        }

        fun create(songs: ArrayList<Song>): DeleteSongsDialog {
            val dialog = DeleteSongsDialog()
            val args = Bundle()
            args.putParcelableArrayList("songs", songs)
            dialog.arguments = args
            return dialog
        }
    }
}

