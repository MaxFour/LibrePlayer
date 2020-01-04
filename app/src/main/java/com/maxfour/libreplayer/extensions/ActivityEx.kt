package com.maxfour.libreplayer.extensions

import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.maxfour.appthemehelper.util.ATHUtil
import com.maxfour.appthemehelper.util.ToolbarContentTintHelper
import com.maxfour.libreplayer.R

fun AppCompatActivity.applyToolbar(toolbar: Toolbar) {
    toolbar.apply {
        setNavigationOnClickListener { onBackPressed() }
        setNavigationIcon(R.drawable.ic_keyboard_backspace_black_24dp)
        ToolbarContentTintHelper.colorBackButton(toolbar)
        backgroundTintList = ColorStateList.valueOf(ATHUtil.resolveColor(this@applyToolbar, R.attr.colorSurface))
    }
    setSupportActionBar(toolbar)
}
