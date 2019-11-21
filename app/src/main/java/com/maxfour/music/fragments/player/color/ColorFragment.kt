package com.maxfour.music.fragments.player.color

import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.animation.GlideAnimation
import com.maxfour.appthemehelper.util.ATHUtil
import com.maxfour.appthemehelper.util.ColorUtil
import com.maxfour.appthemehelper.util.MaterialValueHelper
import com.maxfour.appthemehelper.util.ToolbarContentTintHelper
import com.maxfour.music.R
import com.maxfour.music.fragments.base.AbsPlayerFragment
import com.maxfour.music.glide.MusicPlayerColoredTarget
import com.maxfour.music.glide.SongGlideRequest
import com.maxfour.music.glide.palette.BitmapPaletteWrapper
import com.maxfour.music.helper.MusicPlayerRemote
import com.maxfour.music.model.Song
import com.maxfour.music.util.MusicColorUtil
import kotlinx.android.synthetic.main.fragment_color_player.*

class ColorFragment : AbsPlayerFragment() {

	private var lastColor: Int = 0
	private var backgroundColor: Int = 0
	private var playbackControlsFragment: ColorPlaybackControlsFragment? = null
	private var valueAnimator: ValueAnimator? = null

	override fun playerToolbar(): Toolbar {
		return playerToolbar
	}

	override val paletteColor: Int
		get() = backgroundColor

	override fun onColorChanged(color: Int) {

	}

	override fun onFavoriteToggled() {

	}

	override fun onShow() {
		playbackControlsFragment!!.show()
	}

	override fun onHide() {
		playbackControlsFragment!!.hide()
		onBackPressed()
	}

	override fun onBackPressed(): Boolean {
		return false
	}

	override fun toolbarIconColor(): Int {
		return lastColor
	}

	override fun toggleFavorite(song: Song) {
		super.toggleFavorite(song)
		if (song.id == MusicPlayerRemote.currentSong.id) {
			updateIsFavorite()
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		if (valueAnimator != null) {
			valueAnimator!!.cancel()
			valueAnimator = null
		}
	}

	override fun onCreateView(
			inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View? {

		return inflater.inflate(R.layout.fragment_color_player, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setUpSubFragments()
		setUpPlayerToolbar()
	}

	private fun setUpSubFragments() {
		playbackControlsFragment = childFragmentManager.findFragmentById(
				R.id.playbackControlsFragment
		) as ColorPlaybackControlsFragment?

	}

	private fun setUpPlayerToolbar() {
		playerToolbar.apply {
			inflateMenu(R.menu.menu_player)
			setNavigationOnClickListener { requireActivity().onBackPressed() }
			setOnMenuItemClickListener(this@ColorFragment)
			ToolbarContentTintHelper.colorizeToolbar(
					this, ATHUtil.resolveColor(
					context, R.attr.iconColor
			), activity
			)
		}
	}

	override fun onPlayingMetaChanged() {
		super.onPlayingMetaChanged()
		updateSong()
	}

	override fun onServiceConnected() {
		super.onServiceConnected()
		updateSong()
	}

	private fun updateSong() {
		SongGlideRequest.Builder.from(Glide.with(requireActivity()), MusicPlayerRemote.currentSong)
			.checkIgnoreMediaStore(requireContext()).generatePalette(requireContext()).build()
			.into(object : MusicPlayerColoredTarget(playerImage) {
				override fun onColorReady(color: Int) {

				}

				override fun onResourceReady(
						resource: BitmapPaletteWrapper?,
						glideAnimation: GlideAnimation<in BitmapPaletteWrapper>?
				) {
					super.onResourceReady(resource, glideAnimation)
					resource?.let {
						val background = resource.palette.getColor()

						val palette = resource.palette
						val swatch = MusicColorUtil.getSwatch(palette)

						val textColor = MusicColorUtil.getTextColor(palette)
						val backgroundColor = swatch.rgb

						setColors(backgroundColor, textColor)
					}

				}

				override fun onLoadFailed(e: Exception?, errorDrawable: Drawable?) {
					super.onLoadFailed(e, errorDrawable)
					val backgroundColor = defaultFooterColor
					val textColor = if (ColorUtil.isColorLight(defaultFooterColor)) MaterialValueHelper.getPrimaryTextColor(
							context,
							true
					)
					else MaterialValueHelper.getPrimaryTextColor(context, false)

					setColors(backgroundColor, textColor)
				}
			})
	}

	private fun setColors(
			backgroundColor: Int, textColor: Int
	) {
		playbackControlsFragment?.setDark(textColor, backgroundColor)
		colorGradientBackground?.setBackgroundColor(backgroundColor)
		ToolbarContentTintHelper.colorizeToolbar(playerToolbar, textColor, activity)
		lastColor = textColor
		this.backgroundColor = backgroundColor
		playerActivity?.setLightNavigationBar(ColorUtil.isColorLight(backgroundColor))
		callbacks?.onPaletteColorChanged()
	}

	companion object {
		fun newInstance(): ColorFragment {
			val args = Bundle()
			val fragment = ColorFragment()
			fragment.arguments = args
			return fragment
		}
	}
}

fun Palette.getColor(): Int {
	return when {
		darkMutedSwatch != null -> darkMutedSwatch!!.rgb
		mutedSwatch != null -> mutedSwatch!!.rgb
		lightMutedSwatch != null -> lightMutedSwatch!!.rgb
		else -> Palette.Swatch(Color.BLACK, 1).rgb
	}
}
