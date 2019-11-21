package com.maxfour.appthemehelper.common.prefs.supportv7

import android.content.Context
import android.util.AttributeSet
import androidx.preference.EditTextPreference
import com.maxfour.appthemehelper.R

class ATEEditTextPreference @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        defStyleRes: Int = 0
) : EditTextPreference(context, attrs, defStyleAttr, defStyleRes) {

    init {
        layoutResource = R.layout.ate_preference_custom_support
    }
}
