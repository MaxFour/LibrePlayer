package com.maxfour.music.extensions

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.maxfour.appthemehelper.util.ATHUtil
import com.maxfour.appthemehelper.util.ToolbarContentTintHelper
import com.maxfour.music.R

fun AppCompatActivity.applyToolbar(toolbar: Toolbar) {
    toolbar.apply {
        setNavigationOnClickListener { onBackPressed() }
        setNavigationIcon(R.drawable.ic_keyboard_backspace_black_24dp)
        ToolbarContentTintHelper.colorBackButton(toolbar)
        setBackgroundColor(ATHUtil.resolveColor(this@applyToolbar, R.attr.colorPrimary))

    }
    setSupportActionBar(toolbar)
}