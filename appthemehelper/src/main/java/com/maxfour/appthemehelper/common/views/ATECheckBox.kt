package com.maxfour.appthemehelper.common.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatCheckBox
import com.maxfour.appthemehelper.ATH
import com.maxfour.appthemehelper.ThemeStore

class ATECheckBox @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : AppCompatCheckBox(context, attrs, defStyleAttr) {

    init {
        ATH.setTint(this, ThemeStore.accentColor(context))
    }
}
