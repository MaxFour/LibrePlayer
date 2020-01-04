package com.maxfour.libreplayer.fragments.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.preference.Preference
import androidx.preference.TwoStatePreference
import com.maxfour.libreplayer.R
import com.maxfour.libreplayer.util.PreferenceUtil.*

class NowPlayingSettingsFragment : AbsSettingsFragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun invalidateSettings() {
        updateNowPlayingScreenSummary()
        updateAlbumCoverStyleSummary()

        val carouselEffect: TwoStatePreference = findPreference("carousel_effect")!!
        carouselEffect.setOnPreferenceChangeListener { _, newValue ->
            return@setOnPreferenceChangeListener true
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_now_playing_screen)
    }

    private fun updateAlbumCoverStyleSummary() {
        val preference: Preference = findPreference(ALBUM_COVER_STYLE)!!
        preference.setSummary(getInstance(requireContext()).albumCoverStyle.titleRes)
    }

    private fun updateNowPlayingScreenSummary() {
        val preference: Preference = findPreference(NOW_PLAYING_SCREEN_ID)!!
        preference.setSummary(getInstance(requireContext()).nowPlayingScreen.titleRes)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getInstance(requireContext()).registerOnSharedPreferenceChangedListener(this)
        val preference: Preference = findPreference("album_cover_transform")!!
        setSummary(preference)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        getInstance(requireContext()).unregisterOnSharedPreferenceChangedListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        when (key) {
            NOW_PLAYING_SCREEN_ID -> updateNowPlayingScreenSummary()
            ALBUM_COVER_STYLE -> updateAlbumCoverStyleSummary()
            CIRCULAR_ALBUM_ART, CAROUSEL_EFFECT -> invalidateSettings()
        }
    }
}
