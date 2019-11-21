package com.maxfour.appthemehelper.common.prefs.supportv7


import androidx.fragment.app.DialogFragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.maxfour.appthemehelper.common.prefs.supportv7.dialogs.ATEListPreferenceDialogFragmentCompat
import com.maxfour.appthemehelper.common.prefs.supportv7.dialogs.ATEPreferenceDialogFragment

abstract class ATEPreferenceFragmentCompat : PreferenceFragmentCompat() {
    override fun onDisplayPreferenceDialog(preference: Preference) {
        if (callbackFragment is OnPreferenceDisplayDialogCallback) {
            (callbackFragment as OnPreferenceDisplayDialogCallback).onPreferenceDisplayDialog(this, preference)
            return
        }

        if (activity is OnPreferenceDisplayDialogCallback) {
            (activity as OnPreferenceDisplayDialogCallback).onPreferenceDisplayDialog(this, preference)
            return
        }

        if (fragmentManager?.findFragmentByTag("android.support.v7.preference.PreferenceFragment.DIALOG") == null) {
            val dialogFragment = onCreatePreferenceDialog(preference)

            if (dialogFragment != null) {
                dialogFragment.setTargetFragment(this, 0)
                dialogFragment.show(fragmentManager!!, "android.support.v7.preference.PreferenceFragment.DIALOG")
                return
            }
        }

        super.onDisplayPreferenceDialog(preference)
    }

    open fun onCreatePreferenceDialog(preference: Preference): DialogFragment? {
        if (preference is ATEListPreference) {
            return ATEListPreferenceDialogFragmentCompat.newInstance(preference.getKey())
        } else if (preference is ATEDialogPreference) {
            return ATEPreferenceDialogFragment.newInstance(preference.getKey())
        }
        return null
    }
}