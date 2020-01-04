package com.maxfour.libreplayer.fragments

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.maxfour.libreplayer.R

enum class NowPlayingScreen constructor(
    @param:StringRes @field:StringRes
    val titleRes: Int,
    @param:DrawableRes @field:DrawableRes val drawableResId: Int,
    val id: Int
) {

    NORMAL(R.string.normal, R.drawable.np_normal, 0),
    FLAT(R.string.flat, R.drawable.np_flat, 1),
    FIT(R.string.fit, R.drawable.np_fit, 12),
    TINY(R.string.tiny, R.drawable.np_tiny, 7),
    PEAK(R.string.peak, R.drawable.np_peak, 14),

    ADAPTIVE(R.string.adaptive, R.drawable.np_adaptive, 10),
    BLUR(R.string.blur, R.drawable.np_blur, 4),
    BLUR_CARD(R.string.blur_card, R.drawable.np_blur_card, 9),
    CARD(R.string.card, R.drawable.np_card, 6),
    COLOR(R.string.color, R.drawable.np_color, 5),
    FULL(R.string.full, R.drawable.np_full, 2),
    MATERIAL(R.string.material, R.drawable.np_material, 11),
    PLAIN(R.string.plain, R.drawable.np_plain, 3),
    SIMPLE(R.string.simple, R.drawable.np_simple, 8),
}
