package com.maxfour.appthemehelper.common.prefs.supportv7

import android.content.Context
import android.graphics.PorterDuff
import android.util.AttributeSet
import androidx.preference.Preference
import com.maxfour.appthemehelper.ThemeStore

class ATEPreference @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        defStyleRes: Int = 0
) : Preference(context, attrs, defStyleAttr, defStyleRes) {

    init {

        icon?.setColorFilter(ThemeStore.textColorSecondary(context), PorterDuff.Mode.SRC_IN)
    }
}