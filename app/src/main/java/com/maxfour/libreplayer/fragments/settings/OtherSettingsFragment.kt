package com.maxfour.libreplayer.fragments.settings

import android.os.Bundle
import android.view.View
import androidx.preference.Preference

import com.maxfour.libreplayer.R

class OtherSettingsFragment : AbsSettingsFragment() {
    override fun invalidateSettings() {

    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_advanced)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val preference: Preference = findPreference("last_added_interval")!!
        setSummary(preference)
    }
}
