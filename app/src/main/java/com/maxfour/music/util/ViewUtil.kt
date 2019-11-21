package com.maxfour.music.util

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.view.View
import android.view.animation.PathInterpolator
import android.widget.ProgressBar
import android.widget.SeekBar
import androidx.annotation.ColorInt
import androidx.core.view.ViewCompat
import com.maxfour.appthemehelper.ThemeStore
import com.maxfour.appthemehelper.util.ATHUtil
import com.maxfour.appthemehelper.util.ColorUtil
import com.maxfour.appthemehelper.util.MaterialValueHelper
import com.maxfour.music.R
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView


object ViewUtil {

    const val MUSIC_ANIM_TIME = 1000

    fun setProgressDrawable(progressSlider: SeekBar, newColor: Int, thumbTint: Boolean = false) {

        if (thumbTint) {
            progressSlider.thumbTintList = ColorStateList.valueOf(newColor)
        }
        progressSlider.progressTintList = ColorStateList.valueOf(newColor)
    }

    fun setProgressDrawable(progressSlider: ProgressBar, newColor: Int) {

        val ld = progressSlider.progressDrawable as LayerDrawable

        val progress = ld.findDrawableByLayerId(android.R.id.progress)
        progress.setColorFilter(newColor, PorterDuff.Mode.SRC_IN)

        val background = ld.findDrawableByLayerId(android.R.id.background)
        val primaryColor = ATHUtil.resolveColor(progressSlider.context, R.attr.colorPrimary)
        background.setColorFilter(MaterialValueHelper.getPrimaryDisabledTextColor(progressSlider.context, ColorUtil.isColorLight(primaryColor)), PorterDuff.Mode.SRC_IN)

        val secondaryProgress = ld.findDrawableByLayerId(android.R.id.secondaryProgress)
        secondaryProgress?.setColorFilter(ColorUtil.withAlpha(newColor, 0.65f), PorterDuff.Mode.SRC_IN)
    }

    private fun createColorAnimator(target: Any, propertyName: String, @ColorInt startColor: Int, @ColorInt endColor: Int): Animator {
        val animator: ObjectAnimator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            animator = ObjectAnimator.ofArgb(target, propertyName, startColor, endColor)
        } else {
            animator = ObjectAnimator.ofInt(target, propertyName, startColor, endColor)
            animator.setEvaluator(ArgbEvaluator())
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            animator.interpolator = PathInterpolator(0.4f, 0f, 1f, 1f)
        }
        animator.duration = MUSIC_ANIM_TIME.toLong()
        return animator
    }

    fun hitTest(v: View, x: Int, y: Int): Boolean {
        val tx = (ViewCompat.getTranslationX(v) + 0.5f).toInt()
        val ty = (ViewCompat.getTranslationY(v) + 0.5f).toInt()
        val left = v.left + tx
        val right = v.right + tx
        val top = v.top + ty
        val bottom = v.bottom + ty

        return x in left..right && y >= top && y <= bottom
    }

    fun setUpFastScrollRecyclerViewColor(context: Context,
                                         recyclerView: FastScrollRecyclerView, accentColor: Int = ThemeStore.accentColor(context)) {
        recyclerView.setPopupBgColor(accentColor)
        recyclerView.setPopupTextColor(MaterialValueHelper.getPrimaryTextColor(context, ColorUtil.isColorLight(accentColor)))
        recyclerView.setThumbColor(accentColor)
        recyclerView.setTrackColor(Color.TRANSPARENT)
        recyclerView.setTrackColor(ColorUtil.withAlpha(ATHUtil.resolveColor(context, R.attr.colorControlNormal), 0.12f))

    }

    fun convertDpToPixel(dp: Float, resources: Resources): Float {
        val metrics = resources.displayMetrics
        return dp * metrics.density
    }
}