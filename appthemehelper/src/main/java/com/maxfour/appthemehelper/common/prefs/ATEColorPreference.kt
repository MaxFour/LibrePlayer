package com.maxfour.appthemehelper.common.prefs

import android.content.Context
import android.preference.Preference
import android.util.AttributeSet
import android.view.View
import com.maxfour.appthemehelper.R

class ATEColorPreference @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        defStyleRes: Int = 0
) : Preference(context, attrs, defStyleAttr, defStyleRes) {

    private var mView: View? = null
    private var color: Int = 0
    private var border: Int = 0

    init {
        layoutResource = R.layout.ate_preference_custom
        widgetLayoutResource = R.layout.ate_preference_color
        isPersistent = false
    }

    override fun onBindView(view: View) {
        super.onBindView(view)
        mView = view
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