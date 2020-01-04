package com.maxfour.libreplayer.views

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.maxfour.libreplayer.R
import kotlinx.android.synthetic.main.list_setting_item_view.view.*

class SettingListItemView : FrameLayout {
    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs)
    }

    private fun init(context: Context, attributeSet: AttributeSet?) {
        View.inflate(context, R.layout.list_setting_item_view, this)
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.SettingListItemView)
        icon as ColorIconsImageView
        if (typedArray.hasValue(R.styleable.SettingListItemView_settingListItemIcon)) {
            icon.setImageDrawable(typedArray.getDrawable(R.styleable.SettingListItemView_settingListItemIcon))
        }
        icon.setIconBackgroundColor(typedArray.getColor(R.styleable.SettingListItemView_settingListItemIconColor, Color.WHITE))
        title.text = typedArray.getText(R.styleable.SettingListItemView_settingListItemTitle)
        text.text = typedArray.getText(R.styleable.SettingListItemView_settingListItemText)
        typedArray.recycle()
    }
}
