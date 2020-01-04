package com.maxfour.libreplayer.fragments.settings

import android.content.Intent
import android.media.audiofx.AudioEffect
import android.os.Bundle
import androidx.preference.Preference
import com.maxfour.libreplayer.R
import com.maxfour.libreplayer.util.NavigationUtil
import com.maxfour.libreplayer.util.PreferenceUtil

class AudioSettings : AbsSettingsFragment() {
    override fun invalidateSettings() {
        val findPreference: Preference = findPreference("equalizer")!!
        if (!hasEqualizer() && PreferenceUtil.getInstance(requireContext()).selectedEqualizer != "libreplayer") {
            findPreference.isEnabled = false
            findPreference.summary = resources.getString(R.string.no_equalizer)
        } else {
            findPreference.isEnabled = true
        }
        findPreference.setOnPreferenceClickListener {
            NavigationUtil.openEqualizer(requireActivity())
            true
        }
    }

    private fun hasEqualizer(): Boolean {
        val effects = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)

        val pm = requireActivity().packageManager
        val ri = pm.resolveActivity(effects, 0)
        return ri != null
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_audio)
    }
}
