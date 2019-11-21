package com.maxfour.music.extensions

import com.maxfour.appthemehelper.util.ColorUtil

fun Int.ripAlpha(): Int {
    return ColorUtil.stripAlpha(this)
}