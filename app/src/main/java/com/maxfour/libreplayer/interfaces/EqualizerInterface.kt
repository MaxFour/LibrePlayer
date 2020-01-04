package com.maxfour.libreplayer.interfaces

import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.Virtualizer

interface EqualizerInterface {
    val bandLevelLow: Int

    val bandLevelHigh: Int

    val numberOfBands: Int

    var isBassBoostEnabled: Boolean

    var bassBoostStrength: Int

    var isVirtualizerEnabled: Boolean

    var virtualizerStrength: Int

    val isRunning: Boolean

    val equalizer: Equalizer

    val bassBoost: BassBoost

    val virtualizer: Virtualizer

    fun getCenterFreq(band: Int): Int

    fun getBandLevel(band: Int): Int

    fun setBandLevel(band: Int, level: Int)

}
