package com.maxfour.music.extensions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.annotation.LayoutRes
import com.maxfour.appthemehelper.ThemeStore
import com.maxfour.appthemehelper.util.TintHelper

@Suppress("UNCHECKED_CAST")
fun <T : View> ViewGroup.inflate(@LayoutRes layout: Int): T {
    return LayoutInflater.from(context).inflate(layout, this, false) as T
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun View.hidden() {
    visibility = View.INVISIBLE
}

fun View.showOrHide(show: Boolean) = if (show) show() else hide()

fun EditText.appHandleColor(): EditText {
    TintHelper.colorHandles(this, ThemeStore.accentColor(context))
    return this
}