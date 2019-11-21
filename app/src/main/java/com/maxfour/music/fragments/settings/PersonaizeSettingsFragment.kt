package com.maxfour.music.fragments.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.preference.Preference
import androidx.preference.TwoStatePreference
import com.maxfour.music.R
import com.maxfour.music.util.PreferenceUtil

class PersonaizeSettingsFragment : AbsSettingsFragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun invalidateSettings() {
        val cornerWindow: TwoStatePreference = findPreference("corner_window")!!
        cornerWindow.setOnPreferenceChangeListener { _, newValue ->
            requireActivity().recreate()
            return@setOnPreferenceChangeListener true
        }
        val toggleFullScreen: TwoStatePreference = findPreference("toggle_full_screen")!!
        toggleFullScreen.setOnPreferenceChangeListener { _, _ ->
            requireActivity().recreate()
            true
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_ui)
        addPreferencesFromResource(R.xml.pref_window)
        addPreferencesFromResource(R.xml.pref_lockscreen)
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
