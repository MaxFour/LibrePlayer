package com.maxfour.music.fragments.settings

import android.os.Bundle
import android.view.View
import androidx.preference.Preference

import com.maxfour.music.R

class OtherSettingsFragment : AbsSettingsFragment() {
    override fun invalidateSettings() {

    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_blacklist)
        addPreferencesFromResource(R.xml.pref_playlists)
        addPreferencesFromResource(R.xml.pref_advanced)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val preference: Preference = findPreference("last_added_interval")!!
        setSummary(preference)
    }
}
