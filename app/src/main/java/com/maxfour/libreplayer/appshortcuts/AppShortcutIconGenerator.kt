package com.maxfour.libreplayer.appshortcuts

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.util.TypedValue
import androidx.annotation.RequiresApi
import com.maxfour.appthemehelper.ThemeStore
import com.maxfour.libreplayer.R
import com.maxfour.libreplayer.util.PlayerUtil
import com.maxfour.libreplayer.util.PreferenceUtil

@RequiresApi(Build.VERSION_CODES.N_MR1)
object AppShortcutIconGenerator {
	fun generateThemedIcon(context: Context, iconId: Int): Icon {
		return if (PreferenceUtil.getInstance(context).coloredAppShortcuts()) {
			generateUserThemedIcon(context, iconId)
		} else {
			generateDefaultThemedIcon(context, iconId)
		}
	}

	private fun generateDefaultThemedIcon(context: Context, iconId: Int): Icon {
		// Return an Icon of iconId with default colors
		return generateThemedIcon(
				context,
				iconId,
				context.getColor(R.color.app_shortcut_default_foreground),
				context.getColor(R.color.app_shortcut_default_background)
		)
	}

	private fun generateUserThemedIcon(context: Context, iconId: Int): Icon {
		// Get background color from context's theme
		val typedColorBackground = TypedValue()
		context.theme.resolveAttribute(android.R.attr.colorBackground, typedColorBackground, true)

		// Return an Icon of iconId with those colors
		return generateThemedIcon(
				context, iconId, ThemeStore.accentColor(context), typedColorBackground.data
		)
	}

	private fun generateThemedIcon(
			context: Context, iconId: Int, foregroundColor: Int, backgroundColor: Int
	): Icon {
		// Get and tint foreground and background drawables
		val vectorDrawable = PlayerUtil.getTintedVectorDrawable(context, iconId, foregroundColor)
		val backgroundDrawable = PlayerUtil.getTintedVectorDrawable(
				context, R.drawable.ic_app_shortcut_background, backgroundColor
		)

		// Squash the two drawables together
		val layerDrawable = LayerDrawable(arrayOf(backgroundDrawable, vectorDrawable))

		// Return as an Icon
		return Icon.createWithBitmap(drawableToBitmap(layerDrawable))
	}

	private fun drawableToBitmap(drawable: Drawable): Bitmap {
		val bitmap = Bitmap.createBitmap(
				drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
		)
		val canvas = Canvas(bitmap)
		drawable.setBounds(0, 0, canvas.width, canvas.height)
		drawable.draw(canvas)
		return bitmap
	}
}
