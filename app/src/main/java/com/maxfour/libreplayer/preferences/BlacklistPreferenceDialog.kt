package com.maxfour.libreplayer.preferences

import android.app.Dialog
import android.content.Context
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Html
import android.util.AttributeSet
import androidx.fragment.app.DialogFragment
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.list.listItems
import com.maxfour.appthemehelper.ThemeStore
import com.maxfour.appthemehelper.common.prefs.supportv7.ATEDialogPreference
import com.maxfour.libreplayer.R
import com.maxfour.libreplayer.dialogs.BlacklistFolderChooserDialog
import com.maxfour.libreplayer.providers.BlacklistStore
import com.maxfour.libreplayer.util.PreferenceUtil
import java.io.File
import java.util.*

class BlacklistPreference : ATEDialogPreference {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        icon?.setColorFilter(ThemeStore.textColorSecondary(context), PorterDuff.Mode.SRC_IN)
    }
}

class BlacklistPreferenceDialog : DialogFragment(), BlacklistFolderChooserDialog.FolderCallback {
    companion object {
        fun newInstance(): BlacklistPreferenceDialog {
            return BlacklistPreferenceDialog()
        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val blacklistFolderChooserDialog = childFragmentManager.findFragmentByTag("FOLDER_CHOOSER") as BlacklistFolderChooserDialog?
        blacklistFolderChooserDialog?.setCallback(this)
        refreshBlacklistData()
        return MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            title(com.maxfour.libreplayer.R.string.blacklist)
            cornerRadius(PreferenceUtil.getInstance(requireContext()).dialogCorner)
            positiveButton(android.R.string.ok) {
                dismiss()
            }
            neutralButton(text = getString(R.string.clear_action)) {
                MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                    title(com.maxfour.libreplayer.R.string.clear_blacklist)
                    message(com.maxfour.libreplayer.R.string.do_you_want_to_clear_the_blacklist)
                    cornerRadius(PreferenceUtil.getInstance(requireContext()).dialogCorner)
                    positiveButton(com.maxfour.libreplayer.R.string.clear_action) {
                        BlacklistStore.getInstance(context).clear()
                        refreshBlacklistData()
                    }
                    negativeButton(android.R.string.cancel)
                }
            }
            negativeButton(R.string.add_action) {
                val dialog = BlacklistFolderChooserDialog.create()
                dialog.setCallback(this@BlacklistPreferenceDialog)
                dialog.show(childFragmentManager, "FOLDER_CHOOSER");
            }
            listItems(items = paths, waitForPositiveButton = false) { _, _, text ->
                MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                    cornerRadius(PreferenceUtil.getInstance(requireContext()).dialogCorner)
                    title(com.maxfour.libreplayer.R.string.remove_from_blacklist)
                    message(text = Html.fromHtml(getString(com.maxfour.libreplayer.R.string.do_you_want_to_remove_from_the_blacklist, text)))
                    positiveButton(com.maxfour.libreplayer.R.string.remove_action) {
                        BlacklistStore.getInstance(context).removePath(File(text.toString()))
                        refreshBlacklistData()
                    }
                    negativeButton(android.R.string.cancel)
                }
            }
            noAutoDismiss()
        }
    }

    private lateinit var paths: ArrayList<String>

    private fun refreshBlacklistData() {
        this.paths = BlacklistStore.getInstance(context!!).paths
        val dialog = dialog as MaterialDialog?
        dialog?.listItems(items = paths)
    }

    override fun onFolderSelection(dialog: BlacklistFolderChooserDialog, folder: File) {
        BlacklistStore.getInstance(context!!).addPath(folder);
        refreshBlacklistData();
    }
}
