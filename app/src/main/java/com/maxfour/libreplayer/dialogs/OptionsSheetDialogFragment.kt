package com.maxfour.libreplayer.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.maxfour.libreplayer.R
import com.maxfour.libreplayer.activities.MainActivity
import com.maxfour.libreplayer.util.NavigationUtil
import com.maxfour.libreplayer.util.PreferenceUtil
import com.maxfour.libreplayer.views.OptionMenuItemView

class OptionsSheetDialogFragment : DialogFragment(), View.OnClickListener {

    override fun onClick(view: View) {
        val mainActivity = activity as MainActivity? ?: return
        when (view.id) {
            R.id.actionFolders -> mainActivity.setMusicChooser(MainActivity.FOLDER)
            R.id.actionLibrary -> mainActivity.setMusicChooser(MainActivity.LIBRARY)
            R.id.actionSettings -> NavigationUtil.goToSettings(mainActivity)
        }
        materialDialog.dismiss()
    }

    private lateinit var actionSettings: OptionMenuItemView
    private lateinit var actionLibrary: OptionMenuItemView
    private lateinit var actionFolders: OptionMenuItemView
    private lateinit var materialDialog: MaterialDialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val layout = LayoutInflater.from(context).inflate(R.layout.fragment_main_options, null)
        actionSettings = layout.findViewById(R.id.actionSettings)
        actionLibrary = layout.findViewById(R.id.actionLibrary)
        actionFolders = layout.findViewById(R.id.actionFolders)


        when (arguments?.getInt(WHICH_ONE)) {
            LIBRARY -> actionLibrary.isSelected = true
            FOLDER -> actionFolders.isSelected = true
        }

        actionSettings.setOnClickListener(this)
        actionLibrary.setOnClickListener(this)
        actionFolders.setOnClickListener(this)

        materialDialog = MaterialDialog(requireActivity(), BottomSheet(LayoutMode.WRAP_CONTENT))
                .show {
                    icon(R.mipmap.ic_music)
                    title(R.string.app_name)
                    customView(view = layout, scrollable = true)
                    cornerRadius(PreferenceUtil.getInstance(requireContext()).dialogCorner)
                }
        return materialDialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.let {
            (requireActivity() as MainActivity).setNavigationbarColorAuto()
            (requireActivity() as MainActivity).setLightNavigationBar(true)
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    companion object {

        private const val WHICH_ONE = "which_one"
        @JvmField
        var LIBRARY: Int = 0
        @JvmField
        var FOLDER: Int = 1

        fun newInstance(selectedId: Int): OptionsSheetDialogFragment {
            val bundle = Bundle()
            bundle.putInt(WHICH_ONE, selectedId)
            val fragment = OptionsSheetDialogFragment()
            fragment.arguments = bundle
            return fragment
        }

        fun newInstance(): OptionsSheetDialogFragment {
            return OptionsSheetDialogFragment()
        }
    }
}
