package com.maxfour.music.views

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.maxfour.appthemehelper.ThemeStore
import com.maxfour.appthemehelper.util.ATHUtil
import com.maxfour.appthemehelper.util.ColorUtil
import com.maxfour.appthemehelper.util.NavigationViewUtil
import com.maxfour.music.R
import com.maxfour.music.util.PreferenceUtil

class BottomNavigationBarTinted @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : BottomNavigationView(context, attrs, defStyleAttr) {

    init {
        labelVisibilityMode = PreferenceUtil.getInstance(context).tabTitleMode
        selectedItemId = PreferenceUtil.getInstance(context).lastPage

        val iconColor = ATHUtil.resolveColor(context, R.attr.iconColor)
        val accentColor = ThemeStore.accentColor(context)
        NavigationViewUtil.setItemIconColors(this, ColorUtil.withAlpha(iconColor, 0.5f), accentColor)
        NavigationViewUtil.setItemTextColors(this, ColorUtil.withAlpha(iconColor, 0.5f), accentColor)

        setOnApplyWindowInsetsListener(null)
    }
}