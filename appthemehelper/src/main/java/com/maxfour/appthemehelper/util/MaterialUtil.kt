package com.maxfour.appthemehelper.util

import android.content.res.ColorStateList
import androidx.appcompat.widget.AppCompatButton
import com.afollestad.materialdialogs.internal.button.DialogActionButton
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.maxfour.appthemehelper.ThemeStore


object MaterialUtil {

    @JvmOverloads
    fun setTint(button: MaterialButton, background: Boolean = true,
                color: Int = ThemeStore.accentColor(button.context)) {

        button.isAllCaps = false
        val context = button.context
        val colorState = ColorStateList.valueOf(color)
        val textColor = ColorStateList.valueOf(MaterialValueHelper.getPrimaryTextColor(context, ColorUtil.isColorLight(color)))


        if (background) {
            button.backgroundTintList = colorState
            button.setTextColor(textColor)
            button.iconTint = textColor
        } else {
            button.setTextColor(colorState)
            button.iconTint = colorState
        }

    }

    fun setTint(textInputLayout: TextInputLayout, background: Boolean = true) {
        val context = textInputLayout.context
        val accentColor = ThemeStore.accentColor(context)
        val colorState = ColorStateList.valueOf(accentColor)

        if (background) {
            textInputLayout.backgroundTintList = colorState
            textInputLayout.defaultHintTextColor = colorState
        } else {
            textInputLayout.boxStrokeColor = accentColor
            textInputLayout.defaultHintTextColor = colorState
            textInputLayout.isHintAnimationEnabled = true
        }

    }

    fun setTint(button: DialogActionButton, color: Int = ThemeStore.accentColor(button.context), background: Boolean = true) {
        val temp = button as AppCompatButton
        val context = temp.context
        val colorState = ColorStateList.valueOf(color)
        val textColor = ColorStateList.valueOf(MaterialValueHelper.getPrimaryTextColor(context, ColorUtil.isColorLight(color)))

        if (background) {
            temp.backgroundTintList = colorState
            temp.setTextColor(textColor)
        } else {
            temp.setTextColor(colorState)
        }
    }
}
