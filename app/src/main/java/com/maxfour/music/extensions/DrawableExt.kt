package com.maxfour.music.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes

fun Context.scaledDrawableResources(@DrawableRes id: Int, @DimenRes width: Int, @DimenRes height: Int): Drawable {
    val w = resources.getDimension(width).toInt()
    val h = resources.getDimension(height).toInt()
    return scaledDrawable(id, w, h)
}

fun Context.scaledDrawable(@DrawableRes id: Int, width: Int, height: Int): Drawable {
    val bmp = BitmapFactory.decodeResource(resources, id)
    val bmpScaled = Bitmap.createScaledBitmap(bmp, width, height, false)
    return BitmapDrawable(resources, bmpScaled)
}

