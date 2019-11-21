package com.maxfour.appthemehelper.common.prefs.supportv7

import android.content.Context
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.View
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.maxfour.appthemehelper.R
import com.maxfour.appthemehelper.ThemeStore
import com.maxfour.appthemehelper.common.prefs.BorderCircleView

class ATEColorPreference @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : Preference(context, attrs, defStyleAttr) {

    private var mView: View? = null
    private var color: Int = 0
    private var border: Int = 0

    init {
        layoutResource = R.layout.ate_preference_custom_support
        widgetLayoutResource = R.layout.ate_preference_color
        isPersistent = false

        icon?.setColorFilter(ThemeStore.textColorSecondary(context), PorterDuff.Mode.SRC_IN)
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        mView = holder.itemView
        invalidateColor()
    }

    fun setColor(color: Int, border: Int) {
        this.color = color
        this.border = border
        invalidateColor()
    }

    private fun invalidateColor() {
        if (mView != null) {
            val circle = mView!!.findViewById<View>(R.id.circle) as BorderCircleView
            if (this.color != 0) {
                circle.visibility = View.VISIBLE
                circle.setBackgroundColor(color)
                circle.setBorderColor(border)
            } else {
                circle.visibility = View.GONE
            }
        }
    }
}