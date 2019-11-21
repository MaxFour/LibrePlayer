package com.maxfour.appthemehelper.common.prefs.supportv7

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceViewHolder
import com.maxfour.appthemehelper.ThemeStore

class ATEPreferenceCategory @JvmOverloads constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int = -1,
        defStyleRes: Int = -1
) : PreferenceCategory(context, attrs, defStyleAttr, defStyleRes) {
    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        val mTitle = holder.itemView.findViewById<TextView>(android.R.id.title)
        mTitle.setTextColor(ThemeStore.accentColor(holder.itemView.context))
        /*mTitle.textSize = dip2px(context, 4f)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mTitle.setTextAppearance(R.style.TextAppearance_MaterialComponents_Overline)
        }*/
    }

    fun dip2px(context: Context, dpVale: Float): Float {
        val scale = context.resources.displayMetrics.density
        return (dpVale * scale + 0.5f)
    }
}
