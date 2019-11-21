package com.maxfour.music.glide

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.request.animation.GlideAnimation
import com.maxfour.appthemehelper.util.ATHUtil
import com.maxfour.music.R
import com.maxfour.music.glide.palette.BitmapPaletteTarget
import com.maxfour.music.glide.palette.BitmapPaletteWrapper
import com.maxfour.music.util.PreferenceUtil
import com.maxfour.music.util.MusicColorUtil

abstract class MusicPlayerColoredTarget(view: ImageView) : BitmapPaletteTarget(view) {

    protected val defaultFooterColor: Int
        get() = ATHUtil.resolveColor(getView().context, R.attr.defaultFooterColor)

    protected val albumArtistFooterColor: Int
        get() = ATHUtil.resolveColor(getView().context, R.attr.cardBackgroundColor)

    abstract fun onColorReady(color: Int)

    override fun onLoadFailed(e: Exception?, errorDrawable: Drawable?) {
        super.onLoadFailed(e, errorDrawable)
        onColorReady(defaultFooterColor)
    }

    override fun onResourceReady(resource: BitmapPaletteWrapper?, glideAnimation: GlideAnimation<in BitmapPaletteWrapper>?) {
        super.onResourceReady(resource, glideAnimation)
        val defaultColor = defaultFooterColor

        resource?.let {
            onColorReady(if (PreferenceUtil.getInstance(getView().context).isDominantColor)
                MusicColorUtil.getDominantColor(it.bitmap, defaultColor)
            else
                MusicColorUtil.getColor(it.palette, defaultColor))
        }
    }
}
