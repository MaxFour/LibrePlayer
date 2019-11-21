package com.maxfour.music.views

import android.content.Context
import android.util.AttributeSet

import com.google.android.material.card.MaterialCardView

class WidthFitSquareCardView : MaterialCardView {
    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    fun forceSquare(z: Boolean) {
        this.forceSquare = z
        requestLayout()
    }

    override fun onMeasure(i: Int, i2: Int) {
        var width = i2
        if (this.forceSquare) {
            width = i
        }
        super.onMeasure(i, width)
    }

    private var forceSquare = true
}
