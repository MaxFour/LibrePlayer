package com.maxfour.libreplayer.fragments.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.preference.Preference
import androidx.preference.TwoStatePreference
import com.maxfour.libreplayer.R
import com.maxfour.libreplayer.util.PreferenceUtil

class PersonalizeSettingsFragment : AbsSettingsFragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun invalidateSettings() {

        val toggleFullScreen: TwoStatePreference = findPreference("toggle_full_screen")!!
        toggleFullScreen.setOnPreferenceChangeListener { _, _ ->
            requireActivity().recreate()
            true
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_ui)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        PreferenceUtil.getInstance(requireContext()).registerOnSharedPreferenceChangedListener(this)

        var preference: Preference? = findPreference("album_grid_style")
        setSummary(preference!!)
        preference = findPreference("artist_grid_style")
        setSummary(preference!!)
        preference = findPreference("home_artist_grid_style")
        setSummary(preference!!)
        preference = findPreference("tab_text_mode")
        setSummary(preference!!)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        PreferenceUtil.getInstance(requireContext()).unregisterOnSharedPreferenceChangedListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        when (key) {
            PreferenceUtil.CAROUSEL_EFFECT -> invalidateSettings()
        }
    }
}
