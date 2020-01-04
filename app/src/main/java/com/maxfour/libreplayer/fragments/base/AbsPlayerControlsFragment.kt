package com.maxfour.libreplayer.fragments.base

import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import com.maxfour.libreplayer.R
import com.maxfour.libreplayer.fragments.VolumeFragment
import com.maxfour.libreplayer.helper.MusicProgressViewUpdateHelper
import com.maxfour.libreplayer.util.PreferenceUtil

abstract class AbsPlayerControlsFragment : AbsMusicServiceFragment(), MusicProgressViewUpdateHelper.Callback {

    protected abstract fun show()

    protected abstract fun hide()

    protected abstract fun updateShuffleState()

    protected abstract fun updateRepeatState()

    protected abstract fun setUpProgressSlider()

    abstract fun setDark(color: Int)

    fun showBonceAnimation(view: View) {
        view.apply {
            clearAnimation()
            scaleX = 0.9f
            scaleY = 0.9f
            visibility = View.VISIBLE
            pivotX = (view.width / 2).toFloat()
            pivotY = (view.height / 2).toFloat()

            animate().setDuration(200)
                    .setInterpolator(DecelerateInterpolator())
                    .scaleX(1.1f)
                    .scaleY(1.1f)
                    .withEndAction {
                        animate().setDuration(200)
                                .setInterpolator(AccelerateInterpolator())
                                .scaleX(1f)
                                .scaleY(1f)
                                .alpha(1f)
                                .start()
                    }
                    .start()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideVolumeIfAvailable()
    }

    protected var volumeFragment: VolumeFragment? = null

    private fun hideVolumeIfAvailable() {
        if (PreferenceUtil.getInstance(requireContext()).volumeToggle) {
            childFragmentManager.beginTransaction().replace(R.id.volumeFragmentContainer, VolumeFragment()).commit()
            childFragmentManager.executePendingTransactions()
            volumeFragment = childFragmentManager.findFragmentById(R.id.volumeFragmentContainer) as VolumeFragment?
        }
    }

    companion object {
        const val SLIDER_ANIMATION_TIME: Long = 400
    }

}
