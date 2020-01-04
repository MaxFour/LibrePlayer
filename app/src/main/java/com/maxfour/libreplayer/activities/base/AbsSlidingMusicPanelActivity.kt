package com.maxfour.libreplayer.activities.base

import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.card.MaterialCardView
import com.maxfour.appthemehelper.util.ATHUtil
import com.maxfour.appthemehelper.util.ColorUtil
import com.maxfour.libreplayer.R
import com.maxfour.libreplayer.extensions.hide
import com.maxfour.libreplayer.extensions.show
import com.maxfour.libreplayer.fragments.MiniPlayerFragment
import com.maxfour.libreplayer.fragments.NowPlayingScreen
import com.maxfour.libreplayer.fragments.NowPlayingScreen.*
import com.maxfour.libreplayer.fragments.base.AbsPlayerFragment
import com.maxfour.libreplayer.fragments.player.adaptive.AdaptiveFragment
import com.maxfour.libreplayer.fragments.player.blur.BlurPlayerFragment
import com.maxfour.libreplayer.fragments.player.card.CardFragment
import com.maxfour.libreplayer.fragments.player.cardblur.CardBlurFragment
import com.maxfour.libreplayer.fragments.player.color.ColorFragment
import com.maxfour.libreplayer.fragments.player.fit.FitFragment
import com.maxfour.libreplayer.fragments.player.flat.FlatPlayerFragment
import com.maxfour.libreplayer.fragments.player.full.FullPlayerFragment
import com.maxfour.libreplayer.fragments.player.material.MaterialFragment
import com.maxfour.libreplayer.fragments.player.normal.PlayerFragment
import com.maxfour.libreplayer.fragments.player.peak.PeakPlayerFragment
import com.maxfour.libreplayer.fragments.player.plain.PlainPlayerFragment
import com.maxfour.libreplayer.fragments.player.simple.SimplePlayerFragment
import com.maxfour.libreplayer.fragments.player.tiny.TinyPlayerFragment
import com.maxfour.libreplayer.helper.MusicPlayerRemote
import com.maxfour.libreplayer.model.CategoryInfo
import com.maxfour.libreplayer.util.DensityUtil
import com.maxfour.libreplayer.util.PreferenceUtil
import com.maxfour.libreplayer.views.BottomNavigationBarTinted
import kotlinx.android.synthetic.main.sliding_music_panel_layout.*

abstract class AbsSlidingMusicPanelActivity : AbsMusicServiceActivity(), AbsPlayerFragment.Callbacks {
	companion object {
		val TAG: String = AbsSlidingMusicPanelActivity::class.java.simpleName
	}

	private lateinit var bottomSheetBehavior: BottomSheetBehavior<MaterialCardView>
	private var miniPlayerFragment: MiniPlayerFragment? = null
	private var playerFragment: AbsPlayerFragment? = null
	private var currentNowPlayingScreen: NowPlayingScreen? = null
	private var navigationBarColor: Int = 0
	private var taskColor: Int = 0
	private var lightStatusBar: Boolean = false
	private var lightNavigationBar: Boolean = false
	private var navigationBarColorAnimator: ValueAnimator? = null
	protected abstract fun createContentView(): View
	private val panelState: Int
		get() = bottomSheetBehavior.state

	private val bottomSheetCallbackList = object : BottomSheetBehavior.BottomSheetCallback() {

		override fun onSlide(bottomSheet: View, slideOffset: Float) {
			setMiniPlayerAlphaProgress(slideOffset)
			dimBackground.show()
			dimBackground.alpha = slideOffset
		}

		override fun onStateChanged(bottomSheet: View, newState: Int) {
			when (newState) {
				BottomSheetBehavior.STATE_EXPANDED -> {
					onPanelExpanded()
				}
				BottomSheetBehavior.STATE_COLLAPSED -> {
					onPanelCollapsed()
					dimBackground.hide()
				}
				else -> {

				}
			}
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(createContentView())

		chooseFragmentForTheme()
		setupSlidingUpPanel()

		updateTabs()

		bottomSheetBehavior = BottomSheetBehavior.from(slidingPanel)

		val themeColor = ATHUtil.resolveColor(this, android.R.attr.windowBackground, Color.GRAY)
		dimBackground.setBackgroundColor(ColorUtil.withAlpha(themeColor, 0.5f))
	}

	override fun onResume() {
		super.onResume()
		if (currentNowPlayingScreen != PreferenceUtil.getInstance(this).nowPlayingScreen) {
			postRecreate()
		}
		bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallbackList)

		if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
			setMiniPlayerAlphaProgress(1f)
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		bottomSheetBehavior.removeBottomSheetCallback(bottomSheetCallbackList)
		if (navigationBarColorAnimator != null) navigationBarColorAnimator?.cancel() // just in case
	}

	protected fun wrapSlidingMusicPanel(@LayoutRes resId: Int): View {
		val slidingMusicPanelLayout = layoutInflater.inflate(R.layout.sliding_music_panel_layout, null)
		val contentContainer = slidingMusicPanelLayout.findViewById<ViewGroup>(R.id.mainContentFrame)
		layoutInflater.inflate(resId, contentContainer)
		return slidingMusicPanelLayout
	}

	private fun collapsePanel() {
		bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
	}

	fun expandPanel() {
		bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
		setMiniPlayerAlphaProgress(1f)
	}

	private fun setMiniPlayerAlphaProgress(progress: Float) {
		if (miniPlayerFragment?.view == null) return
		val alpha = 1 - progress
		miniPlayerFragment?.view?.alpha = alpha
		// necessary to make the views below clickable
		miniPlayerFragment?.view?.visibility = if (alpha == 0f) View.GONE else View.VISIBLE

		bottomNavigationView.translationY = progress * 500
		//bottomNavigationView.alpha = alpha
	}

	open fun onPanelCollapsed() {
		// restore values
		super.setLightStatusbar(lightStatusBar)
		super.setTaskDescriptionColor(taskColor)
		super.setNavigationbarColor(navigationBarColor)
		super.setLightNavigationBar(lightNavigationBar)


		playerFragment?.setMenuVisibility(false)
		playerFragment?.userVisibleHint = false
		playerFragment?.onHide()
	}

	open fun onPanelExpanded() {
		val playerFragmentColor = playerFragment!!.paletteColor
		super.setTaskDescriptionColor(playerFragmentColor)

		playerFragment?.setMenuVisibility(true)
		playerFragment?.userVisibleHint = true
		playerFragment?.onShow()
		onPaletteColorChanged()
	}

	private fun setupSlidingUpPanel() {
		slidingPanel.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
			override fun onGlobalLayout() {
				slidingPanel.viewTreeObserver.removeOnGlobalLayoutListener(this)
				if (currentNowPlayingScreen != PEAK) {
					val params = slidingPanel.layoutParams as ViewGroup.LayoutParams
					params.height = ViewGroup.LayoutParams.MATCH_PARENT
					slidingPanel.layoutParams = params
				}
				when (panelState) {
					BottomSheetBehavior.STATE_EXPANDED -> onPanelExpanded()
					BottomSheetBehavior.STATE_COLLAPSED -> onPanelCollapsed()
					else -> playerFragment!!.onHide()
				}
			}
		})
	}

	fun toggleBottomNavigationView(toggle: Boolean) {
		bottomNavigationView.visibility = if (toggle) View.GONE else View.VISIBLE
	}

	fun getBottomNavigationView(): BottomNavigationBarTinted {
		return bottomNavigationView
	}

	private fun hideBottomBar(hide: Boolean) {
		val heightOfBar = resources.getDimensionPixelSize(R.dimen.mini_player_height)
		val heightOfBarWithTabs = resources.getDimensionPixelSize(R.dimen.mini_player_height_expanded)

		if (hide) {
			bottomSheetBehavior.isHideable = true
			bottomSheetBehavior.peekHeight = 0
			collapsePanel()
			bottomNavigationView.elevation = DensityUtil.dip2px(this, 10f).toFloat()
		} else {
			if (MusicPlayerRemote.playingQueue.isNotEmpty()) {
				slidingPanel.cardElevation = DensityUtil.dip2px(this, 10f).toFloat()
				bottomNavigationView.elevation = DensityUtil.dip2px(this, 10f).toFloat()
				bottomSheetBehavior.isHideable = false
				bottomSheetBehavior.peekHeight =
						if (bottomNavigationView.visibility == View.VISIBLE) heightOfBarWithTabs else heightOfBar
			}
		}
	}

	fun setBottomBarVisibility(gone: Int) {
		bottomNavigationView.visibility = gone
		hideBottomBar(false)
	}

	private fun chooseFragmentForTheme() {
		currentNowPlayingScreen = PreferenceUtil.getInstance(this).nowPlayingScreen

		val fragment: Fragment = when (currentNowPlayingScreen) {
			BLUR -> BlurPlayerFragment()
			ADAPTIVE -> AdaptiveFragment()
			NORMAL -> PlayerFragment()
			CARD -> CardFragment()
			BLUR_CARD -> CardBlurFragment()
			FIT -> FitFragment()
			FLAT -> FlatPlayerFragment()
			FULL -> FullPlayerFragment()
			PLAIN -> PlainPlayerFragment()
			SIMPLE -> SimplePlayerFragment()
			MATERIAL -> MaterialFragment()
			COLOR -> ColorFragment()
			TINY -> TinyPlayerFragment()
			PEAK -> PeakPlayerFragment()
			else -> PlayerFragment()
		} // must implement AbsPlayerFragment
		supportFragmentManager.beginTransaction().replace(R.id.playerFragmentContainer, fragment)
			.commit()
		supportFragmentManager.executePendingTransactions()

		playerFragment = supportFragmentManager.findFragmentById(R.id.playerFragmentContainer) as AbsPlayerFragment
		miniPlayerFragment = supportFragmentManager.findFragmentById(R.id.miniPlayerFragment) as MiniPlayerFragment
		miniPlayerFragment?.view?.setOnClickListener { expandPanel() }
	}

	override fun onServiceConnected() {
		super.onServiceConnected()
		if (MusicPlayerRemote.playingQueue.isNotEmpty()) {
			slidingPanel.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
				override fun onGlobalLayout() {
					slidingPanel.viewTreeObserver.removeOnGlobalLayoutListener(this)
					hideBottomBar(false)
				}
			})
		} // don't call hideBottomBar(true) here as it causes a bug with the SlidingUpPanelLayout
	}

	override fun onQueueChanged() {
		super.onQueueChanged()
		hideBottomBar(MusicPlayerRemote.playingQueue.isEmpty())
	}

	override fun onBackPressed() {
		if (!handleBackPress()) super.onBackPressed()
	}

	open fun handleBackPress(): Boolean {
		if (bottomSheetBehavior.peekHeight != 0 && playerFragment!!.onBackPressed()) return true
		if (panelState == BottomSheetBehavior.STATE_EXPANDED) {
			collapsePanel()
			return true
		}
		return false
	}

	override fun onPaletteColorChanged() {
		if (panelState == BottomSheetBehavior.STATE_EXPANDED) {
			val paletteColor = playerFragment!!.paletteColor
			super.setTaskDescriptionColor(paletteColor)

			val isColorLight = ColorUtil.isColorLight(paletteColor)

			if (PreferenceUtil.getInstance(this).adaptiveColor && (currentNowPlayingScreen == NORMAL || currentNowPlayingScreen == FLAT)) {
				super.setLightNavigationBar(true)
				super.setLightStatusbar(isColorLight)
			} else if (currentNowPlayingScreen == FULL || currentNowPlayingScreen == CARD || currentNowPlayingScreen == FIT || currentNowPlayingScreen == BLUR || currentNowPlayingScreen == BLUR_CARD) {
				super.setLightStatusbar(false)
				super.setLightNavigationBar(true)
			} else if (currentNowPlayingScreen == COLOR || currentNowPlayingScreen == TINY) {
				super.setNavigationbarColor(paletteColor)
				super.setLightNavigationBar(isColorLight)
				super.setLightStatusbar(isColorLight)
			} else {
				super.setLightStatusbar(
					ColorUtil.isColorLight(
						ATHUtil.resolveColor(
					this,
							android.R.attr.windowBackground
						)
					)
				)
				super.setLightNavigationBar(true)
			}
		}
	}

	override fun setLightStatusbar(enabled: Boolean) {
		lightStatusBar = enabled
		if (panelState == BottomSheetBehavior.STATE_COLLAPSED) {
			super.setLightStatusbar(enabled)
		}
	}

	override fun setLightNavigationBar(enabled: Boolean) {
		lightNavigationBar = enabled
		if (panelState == BottomSheetBehavior.STATE_COLLAPSED) {
			super.setLightNavigationBar(enabled)
		}
	}

	override fun setNavigationbarColor(color: Int) {
		navigationBarColor = color
		if (panelState == BottomSheetBehavior.STATE_COLLAPSED) {
			if (navigationBarColorAnimator != null) navigationBarColorAnimator!!.cancel()
			super.setNavigationbarColor(color)
		}
	}

	override fun setTaskDescriptionColor(color: Int) {
		taskColor = color
		if (panelState == BottomSheetBehavior.STATE_COLLAPSED) {
			super.setTaskDescriptionColor(color)
		}
	}

	private fun updateTabs() {
		bottomNavigationView.menu.clear()
		val currentTabs: List<CategoryInfo> = PreferenceUtil.getInstance(this).libraryCategoryInfos
		for (tab in currentTabs) {
			if (tab.visible) {
				val menu = tab.category
				bottomNavigationView.menu.add(0, menu.id, 0, menu.stringRes).setIcon(menu.icon)
			}
		}
		print("Tabs -> ${currentTabs.size}")
		if (currentTabs.size <= 1) {
			toggleBottomNavigationView(true)
		}
	}

	/*override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
		if (ev?.action == MotionEvent.ACTION_DOWN) {
			if (panelState == BottomSheetBehavior.STATE_EXPANDED) {
				val outRect = Rect()
				slidingPanel.getGlobalVisibleRect(outRect)
				if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
					bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
				}
			}
		}
		return super.dispatchTouchEvent(ev)
	}*/
}
