package com.maxfour.music.views

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.maxfour.appthemehelper.util.ATHUtil
import com.maxfour.appthemehelper.util.ColorUtil
import com.maxfour.music.R
import com.maxfour.music.util.MusicColorUtil
import com.maxfour.music.util.PreferenceUtil

class ColorIconsImageView : AppCompatImageView {

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        // Load the styled attributes and set their properties
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.ColorIconsImageView, 0, 0)
        val color = attributes.getColor(R.styleable.ColorIconsImageView_iconBackgroundColor, Color.RED);
        setIconBackgroundColor(color)
        attributes.recycle()
    }

    private fun setIconBackgroundColor(color: Int) {
        setBackgroundResource(R.drawable.color_circle_gradient)
        if (ATHUtil.isWindowBackgroundDark(context) && PreferenceUtil.getInstance(context).desaturatedColor()) {
            val desaturatedColor = MusicColorUtil.desaturateColor(color, 0.4f)
            backgroundTintList = ColorStateList.valueOf(desaturatedColor)
            imageTintList = ColorStateList.valueOf(ATHUtil.resolveColor(context, R.attr.colorPrimary))
        } else {
            backgroundTintList = ColorStateList.valueOf(ColorUtil.adjustAlpha(color, 0.22f))
            imageTintList = ColorStateList.valueOf(ColorUtil.withAlpha(color, 0.75f))
        }
        requestLayout()
        invalidate()
    }

}
