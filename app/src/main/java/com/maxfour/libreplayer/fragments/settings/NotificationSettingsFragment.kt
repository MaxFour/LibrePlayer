package com.maxfour.libreplayer.fragments.settings

import android.content.SharedPreferences
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.TwoStatePreference
import com.maxfour.libreplayer.R
import com.maxfour.libreplayer.util.PreferenceUtil

class NotificationSettingsFragment : AbsSettingsFragment(), SharedPreferences.OnSharedPreferenceChangeListener {
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == PreferenceUtil.CLASSIC_NOTIFICATION) {
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                findPreference<Preference>("colored_notification")?.isEnabled = sharedPreferences?.getBoolean(key, false)!!
            }
        }
    }

    override fun invalidateSettings() {

        val classicNotification: TwoStatePreference? = findPreference("classic_notification")
        if (VERSION.SDK_INT < VERSION_CODES.N) {
            classicNotification?.isVisible = false
        } else {
            classicNotification?.apply {
                isChecked = PreferenceUtil.getInstance(requireContext()).classicNotification()
                setOnPreferenceChangeListener { _, newValue ->
                    // Save preference
                    PreferenceUtil.getInstance(requireContext()).setClassicNotification(newValue as Boolean)
                    invalidateSettings()
                    true
                }
            }
        }

        val coloredNotification: TwoStatePreference? = findPreference("colored_notification")
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            coloredNotification?.isEnabled = PreferenceUtil.getInstance(requireContext()).classicNotification()
        } else {
            coloredNotification?.apply {
                isChecked = PreferenceUtil.getInstance(requireContext()).coloredNotification()
                setOnPreferenceChangeListener { _, newValue ->
                    PreferenceUtil.getInstance(requireContext()).setColoredNotification(newValue as Boolean)
                    true
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        PreferenceUtil.getInstance(requireContext()).registerOnSharedPreferenceChangedListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        PreferenceUtil.getInstance(requireContext()).unregisterOnSharedPreferenceChangedListener(this)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_notification)
    }
}
