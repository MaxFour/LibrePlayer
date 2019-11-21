package com.maxfour.music.util

import android.content.Context
import android.content.res.Configuration.UI_MODE_NIGHT_MASK
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.PowerManager
import androidx.annotation.StyleRes
import com.maxfour.music.R

object ThemeManager {

    @StyleRes
    fun getThemeResValue(context: Context): Int = when (PreferenceUtil.getInstance(context).generalThemeValue) {
        "light" -> R.style.Theme_Music_Light
        "auto" -> if (isSystemDarkModeEnabled(context)) R.style.Theme_Music else R.style.Theme_Music_Light
        "black" -> R.style.Theme_Music_Black
        else -> R.style.Theme_Music
        /**
         * To add a toggle for amoled theme just add an if statement such as
         * if(PreferenceUtil.getInstance(context).useAmoled) blablabla
         */
    }

    private fun isSystemDarkModeEnabled(context: Context): Boolean {
        val isBatterySaverEnabled = (context.getSystemService(Context.POWER_SERVICE) as PowerManager?)?.isPowerSaveMode
                ?: false
        val isDarkModeEnabled = (context.resources.configuration.uiMode and UI_MODE_NIGHT_MASK) == UI_MODE_NIGHT_YES

        return isBatterySaverEnabled or isDarkModeEnabled
    }

}