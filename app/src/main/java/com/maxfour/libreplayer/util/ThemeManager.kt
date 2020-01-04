package com.maxfour.libreplayer.util

import android.content.Context
import android.content.res.Configuration.UI_MODE_NIGHT_MASK
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.PowerManager
import androidx.annotation.StyleRes
import com.maxfour.libreplayer.R

object ThemeManager {

    @StyleRes
    fun getThemeResValue(context: Context): Int = when (PreferenceUtil.getInstance(context).generalThemeValue) {
        "light" -> R.style.Theme_Player_Light
        "dark" -> R.style.Theme_Player_Base
        "auto" -> R.style.Theme_Player_FollowSystem
        "black" -> R.style.Theme_Player_Black
        else -> R.style.Theme_Player_FollowSystem
    }

    private fun isSystemDarkModeEnabled(context: Context): Boolean {
        val isBatterySaverEnabled = (context.getSystemService(Context.POWER_SERVICE) as PowerManager?)?.isPowerSaveMode
                ?: false
        val isDarkModeEnabled = (context.resources.configuration.uiMode and UI_MODE_NIGHT_MASK) == UI_MODE_NIGHT_YES

        return isBatterySaverEnabled or isDarkModeEnabled
    }

}
