package com.maxfour.appthemehelper.common.views

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.textview.MaterialTextView
import com.maxfour.appthemehelper.ThemeStore

class ATESecondaryTextView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : MaterialTextView(context, attrs, defStyleAttr) {

    init {
        setTextColor(ThemeStore.textColorSecondary(context))
    }
}
