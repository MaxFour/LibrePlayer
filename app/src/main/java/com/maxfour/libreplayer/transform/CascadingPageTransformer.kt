package com.maxfour.libreplayer.transform

import android.annotation.SuppressLint
import android.view.View
import androidx.viewpager.widget.ViewPager

class CascadingPageTransformer : ViewPager.PageTransformer {

    private var mScaleOffset = 40

    fun setScaleOffset(mScaleOffset: Int) {
        this.mScaleOffset = mScaleOffset
    }

    @SuppressLint("NewApi")
    override fun transformPage(page: View, position: Float) {
        if (position <= 0.0f) {
            page.translationX = 0f
            page.rotation = 45 * position
            page.translationX = page.width / 3 * position
        } else if (position <= 1f) {
            val scale = (page.width - mScaleOffset * position) / page.width.toFloat()

            page.scaleX = scale
            page.scaleY = scale

            page.translationX = -page.width * position
            page.translationY = mScaleOffset * 0.8f * position
        } else {
            val scale = (page.width - mScaleOffset * position) / page.width.toFloat()

            page.scaleX = scale
            page.scaleY = scale

            page.translationX = -page.width * position
            page.translationY = mScaleOffset * 0.7f * position
        }
    }
}
