package com.maxfour.libreplayer.preferences

import android.app.Dialog
import android.content.Context
import android.content.res.TypedArray
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.AttributeSet
import androidx.preference.ListPreference
import androidx.preference.PreferenceDialogFragmentCompat
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.maxfour.appthemehelper.ThemeStore
import com.maxfour.libreplayer.R
import com.maxfour.libreplayer.util.PreferenceUtil

class MaterialListPreference : ListPreference {
    private val mLayoutRes = R.layout.ate_preference_list

    constructor(context: Context) : super(context) {
        init(context)
    }

    private fun init(context: Context) {
        icon?.setColorFilter(ThemeStore.textColorSecondary(context), PorterDuff.Mode.SRC_IN)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context)
    }

    override fun getDialogLayoutResource(): Int {
        return mLayoutRes
    }

    override fun onGetDefaultValue(a: TypedArray, index: Int): String {
        // Default value from attribute. Fallback value is set to 0.
        return a.getString(index)!!
    }

    fun setCustomValue(any: Any) {
        when (any) {
            is String -> persistString(any)
            is Int -> persistInt(any)
            is Boolean -> persistBoolean(any)
        }
    }
}

class MaterialListPreferenceDialog : PreferenceDialogFragmentCompat() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val materialListPreference = preference as MaterialListPreference
        val entries = arguments?.getStringArrayList(EXTRA_ENTRIES)
        val entriesValues = arguments?.getStringArrayList(EXTRA_ENTRIES_VALUES)
        val position: Int = arguments?.getInt(EXTRA_POSITION) ?: 0
        materialDialog = MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT))
                .title(text = materialListPreference.title.toString())
                .positiveButton(R.string.apply)
                .cornerRadius(PreferenceUtil.getInstance(requireContext()).dialogCorner)
                .listItemsSingleChoice(items = entries, initialSelection = position, waitForPositiveButton = true) { _, index, _ ->
                    entriesValues?.let {
                        materialListPreference.callChangeListener(it[index])
                        materialListPreference.setCustomValue(it[index])
                    }
                    entries?.let {
                        materialListPreference.summary = it[index]
                        val value = materialListPreference.entryValues[index].toString()
                        if (materialListPreference.callChangeListener(value)) {
                            materialListPreference.value = value
                        }
                    }
                    dismiss()
                }
        return materialDialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        materialDialog.dismiss()
    }

    private lateinit var materialDialog: MaterialDialog

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            materialDialog.dismiss()
        }
    }

    companion object {

        private const val EXTRA_KEY = "key"
        private const val EXTRA_TITLE = "title"
        private const val EXTRA_POSITION = "position"
        private const val EXTRA_ENTRIES = "extra_entries"
        private const val EXTRA_ENTRIES_VALUES = "extra_entries_values"

        fun newInstance(listPreference: ListPreference): MaterialListPreferenceDialog {
            val entries = listPreference.entries.toList() as ArrayList<String>
            val entriesValues = listPreference.entryValues.toList() as ArrayList<String>
            println("List value: ${listPreference.value}")
            val position = listPreference.findIndexOfValue(listPreference.value)
            val args = Bundle()
            args.putString(ARG_KEY, listPreference.key)
            args.putString(EXTRA_TITLE, listPreference.title.toString())
            args.putInt(EXTRA_POSITION, position)
            args.putStringArrayList(EXTRA_ENTRIES, entries)
            args.putStringArrayList(EXTRA_ENTRIES_VALUES, entriesValues)
            val fragment = MaterialListPreferenceDialog()
            fragment.arguments = args
            return fragment
        }
    }
}
