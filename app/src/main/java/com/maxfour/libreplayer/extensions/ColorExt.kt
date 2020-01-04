package com.maxfour.libreplayer.extensions

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.widget.Toolbar
import com.maxfour.appthemehelper.util.ATHUtil
import com.maxfour.appthemehelper.util.ColorUtil
import com.maxfour.libreplayer.R

fun Int.ripAlpha(): Int {
    return ColorUtil.stripAlpha(this)
}

fun Any.surfaceColor(context: Context): Int {
    return ATHUtil.resolveColor(context, R.attr.colorSurface, Color.WHITE)
}

fun Toolbar.backgroundTintList() {
    val surfaceColor = ATHUtil.resolveColor(context, R.attr.colorSurface, Color.BLACK)
    val colorStateList = ColorStateList.valueOf(surfaceColor)
    backgroundTintList = colorStateList
}
