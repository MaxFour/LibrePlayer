package com.maxfour.libreplayer.views

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.RippleDrawable
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.maxfour.appthemehelper.ThemeStore
import com.maxfour.appthemehelper.util.ATHUtil
import com.maxfour.appthemehelper.util.ColorUtil
import com.maxfour.appthemehelper.util.NavigationViewUtil
import com.maxfour.libreplayer.R
import com.maxfour.libreplayer.util.PreferenceUtil
import com.maxfour.libreplayer.util.RippleUtils

class BottomNavigationBarTinted @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : BottomNavigationView(context, attrs, defStyleAttr) {

    init {
        labelVisibilityMode = PreferenceUtil.getInstance(context).tabTitleMode
        selectedItemId = PreferenceUtil.getInstance(context).lastPage

        val iconColor = ATHUtil.resolveColor(context, android.R.attr.colorControlNormal)
        val accentColor = ThemeStore.accentColor(context)
        NavigationViewUtil.setItemIconColors(this, ColorUtil.withAlpha(iconColor, 0.5f), accentColor)
        NavigationViewUtil.setItemTextColors(this, ColorUtil.withAlpha(iconColor, 0.5f), accentColor)
        itemBackground = RippleDrawable(RippleUtils.convertToRippleDrawableColor(ColorStateList.valueOf(ThemeStore.accentColor(context).addAlpha())), ContextCompat.getDrawable(context, R.drawable.bottom_navigation_item_background), ContextCompat.getDrawable(context, R.drawable.bottom_navigation_item_background_mask))
        setOnApplyWindowInsetsListener(null)
        //itemRippleColor = ColorStateList.valueOf(accentColor)
        background  = ColorDrawable(ATHUtil.resolveColor(context, R.attr.colorSurface))
    }
}

private fun Int.addAlpha(): Int {
    return ColorUtil.withAlpha(this, 0.12f)
}
