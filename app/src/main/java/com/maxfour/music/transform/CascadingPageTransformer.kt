package com.maxfour.music.transform

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
        if (position <= 0.0f) {//被滑动的那页  position 是-下标~ 0
            page.translationX = 0f
            //旋转角度  45° * -0.1 = -4.5°
            page.rotation = 45 * position
            //X轴偏移 li:  300/3 * -0.1 = -10
            page.translationX = page.width / 3 * position
        } else if (position <= 1f) {
            val scale = (page.width - mScaleOffset * position) / page.width.toFloat()

            page.scaleX = scale
            page.scaleY = scale

            page.translationX = -page.width * position
            page.translationY = mScaleOffset * 0.8f * position
        } else {
            //缩放比例
            val scale = (page.width - mScaleOffset * position) / page.width.toFloat()

            page.scaleX = scale
            page.scaleY = scale

            page.translationX = -page.width * position
            page.translationY = mScaleOffset * 0.7f * position
        }
    }
} 