package com.maxfour.music.preferences

import android.app.Dialog
import android.content.Context
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.AttributeSet
import android.widget.Toast
import androidx.preference.PreferenceDialogFragmentCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.maxfour.appthemehelper.ThemeStore
import com.maxfour.appthemehelper.common.prefs.supportv7.ATEDialogPreference
import com.maxfour.music.R
import com.maxfour.music.adapter.CategoryInfoAdapter
import com.maxfour.music.model.CategoryInfo
import com.maxfour.music.util.PreferenceUtil
import java.util.*

class LibraryPreference : ATEDialogPreference {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        icon?.setColorFilter(ThemeStore.textColorSecondary(context), PorterDuff.Mode.SRC_IN)
    }
}

class LibraryPreferenceDialog : PreferenceDialogFragmentCompat() {

    override fun onDialogClosed(positiveResult: Boolean) {

    }

    lateinit var adapter: CategoryInfoAdapter

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = requireActivity().layoutInflater.inflate(R.layout.preference_dialog_library_categories, null)

        val categoryInfos: List<CategoryInfo>
        if (savedInstanceState != null) {
            categoryInfos = savedInstanceState.getParcelableArrayList(PreferenceUtil.LIBRARY_CATEGORIES)!!
        } else {
            categoryInfos = PreferenceUtil.getInstance(requireContext()).libraryCategoryInfos
        }
        adapter = CategoryInfoAdapter(categoryInfos)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter

        adapter.attachToRecyclerView(recyclerView)

        return MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT))
                .title(R.string.library_categories)
                .cornerRadius(PreferenceUtil.getInstance(requireContext()).dialogCorner)
                .customView(view = view)
                .positiveButton(android.R.string.ok) {
                    updateCategories(adapter.categoryInfos)
                    dismiss()
                }
                .negativeButton(android.R.string.cancel) {
                    dismiss()
                }
                .neutralButton(R.string.reset_action) {
                    adapter.categoryInfos = PreferenceUtil.getInstance(requireContext()).defaultLibraryCategoryInfos
                }
                .noAutoDismiss()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(PreferenceUtil.LIBRARY_CATEGORIES, ArrayList(adapter.categoryInfos))
    }

    private fun updateCategories(categories: List<CategoryInfo>) {
        if (getSelected(categories) == 0) return
        if (getSelected(categories) > 5) {
            Toast.makeText(context, "Not more than 5 items", Toast.LENGTH_SHORT).show()
            return
        }
        PreferenceUtil.getInstance(requireContext()).libraryCategoryInfos = categories
    }

    private fun getSelected(categories: List<CategoryInfo>): Int {
        var selected = 0
        for (categoryInfo in categories) {
            if (categoryInfo.visible)
                selected++
        }
        return selected
    }

    companion object {

        fun newInstance(key: String): LibraryPreferenceDialog {
            val bundle = Bundle()
            bundle.putString(ARG_KEY, key)
            val fragment = LibraryPreferenceDialog()
            fragment.arguments = bundle
            return fragment
        }
    }
}