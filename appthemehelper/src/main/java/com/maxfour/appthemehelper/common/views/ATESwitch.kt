package com.maxfour.appthemehelper.common.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.SwitchCompat
import com.maxfour.appthemehelper.ATH
import com.maxfour.appthemehelper.ThemeStore

class ATESwitch : SwitchCompat {

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        ATH.setTint(this, ThemeStore.accentColor(context))
    }

    override fun isShown(): Boolean {
        return parent != null && visibility == View.VISIBLE
    }
}
