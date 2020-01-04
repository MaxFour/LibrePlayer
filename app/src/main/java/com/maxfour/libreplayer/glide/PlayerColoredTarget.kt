package com.maxfour.libreplayer.glide

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.request.animation.GlideAnimation
import com.maxfour.appthemehelper.util.ATHUtil
import com.maxfour.libreplayer.R
import com.maxfour.libreplayer.glide.palette.BitmapPaletteTarget
import com.maxfour.libreplayer.glide.palette.BitmapPaletteWrapper
import com.maxfour.libreplayer.util.PlayerColorUtil
import com.maxfour.libreplayer.util.PreferenceUtil

abstract class PlayerColoredTarget(view: ImageView) : BitmapPaletteTarget(view) {

    protected val defaultFooterColor: Int
        get() = ATHUtil.resolveColor(getView().context, R.attr.colorSurface)

    protected val albumArtistFooterColor: Int
        get() = ATHUtil.resolveColor(getView().context, R.attr.colorSurface)

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
                PlayerColorUtil.getDominantColor(it.bitmap, defaultColor)
            else
                PlayerColorUtil.getColor(it.palette, defaultColor))
        }
    }
}
