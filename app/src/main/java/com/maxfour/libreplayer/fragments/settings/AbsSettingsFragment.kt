package com.maxfour.libreplayer.fragments.settings

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import com.maxfour.appthemehelper.common.prefs.supportv7.ATEPreferenceFragmentCompat
import com.maxfour.appthemehelper.util.ATHUtil
import com.maxfour.libreplayer.R
import com.maxfour.libreplayer.preferences.*

abstract class AbsSettingsFragment : ATEPreferenceFragmentCompat() {

    internal fun setSummary(preference: Preference, value: Any?) {
        val stringValue = value.toString()
        if (preference is ListPreference) {
            val index = preference.findIndexOfValue(stringValue)
            preference.setSummary(if (index >= 0) preference.entries[index] else null)
        } else {
            preference.summary = stringValue
        }
    }

    abstract fun invalidateSettings()

    protected fun setSummary(preference: Preference?) {
        preference?.let {
            setSummary(it, PreferenceManager
                    .getDefaultSharedPreferences(it.context)
                    .getString(it.key, ""))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDivider(ColorDrawable(Color.TRANSPARENT))
        listView.setBackgroundColor(ATHUtil.resolveColor(requireContext(), R.attr.colorSurface))
        listView.overScrollMode = View.OVER_SCROLL_NEVER
        listView.setPadding(0, 0, 0, 0)
        listView.setPaddingRelative(0, 0, 0, 0)
        invalidateSettings()
    }

    override fun onCreatePreferenceDialog(preference: Preference): DialogFragment? {
        return when (preference) {
            is LibraryPreference -> LibraryPreferenceDialog.newInstance(preference.key)
            is NowPlayingScreenPreference -> NowPlayingScreenPreferenceDialog.newInstance(preference.key)
            is AlbumCoverStylePreference -> AlbumCoverStylePreferenceDialog.newInstance(preference.key)
            is MaterialListPreference -> {
                MaterialListPreferenceDialog.newInstance(preference)
            }
            is BlacklistPreference -> BlacklistPreferenceDialog.newInstance()
            else -> super.onCreatePreferenceDialog(preference)
        }
    }
}
