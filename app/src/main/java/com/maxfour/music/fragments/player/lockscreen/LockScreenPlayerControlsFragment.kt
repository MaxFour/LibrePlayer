package com.maxfour.music.fragments.player.lockscreen

import android.animation.ObjectAnimator
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.SeekBar
import com.maxfour.appthemehelper.ThemeStore
import com.maxfour.appthemehelper.util.ATHUtil
import com.maxfour.appthemehelper.util.ColorUtil
import com.maxfour.appthemehelper.util.MaterialValueHelper
import com.maxfour.appthemehelper.util.TintHelper
import com.maxfour.music.R
import com.maxfour.music.extensions.ripAlpha
import com.maxfour.music.fragments.base.AbsPlayerControlsFragment
import com.maxfour.music.helper.MusicPlayerRemote
import com.maxfour.music.helper.MusicProgressViewUpdateHelper
import com.maxfour.music.helper.PlayPauseButtonOnClickHandler
import com.maxfour.music.misc.SimpleOnSeekbarChangeListener
import com.maxfour.music.service.MusicService
import com.maxfour.music.util.MusicUtil
import com.maxfour.music.util.PreferenceUtil
import com.maxfour.music.util.ViewUtil
import kotlinx.android.synthetic.main.fragment_lock_screen_playback_controls.*

class LockScreenPlayerControlsFragment : AbsPlayerControlsFragment() {

    private var progressViewUpdateHelper: MusicProgressViewUpdateHelper? = null
    private var lastPlaybackControlsColor: Int = 0
    private var lastDisabledPlaybackControlsColor: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressViewUpdateHelper = MusicProgressViewUpdateHelper(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_lock_screen_playback_controls, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpMusicControllers()
    }

    private fun updateSong() {
        val song = MusicPlayerRemote.currentSong
        title.text = song.title
        text.text = String.format("%s - %s", song.artistName, song.albumName)

    }

    override fun onResume() {
        super.onResume()
        progressViewUpdateHelper!!.start()
    }

    override fun onPause() {
        super.onPause()
        progressViewUpdateHelper!!.stop()
    }

    override fun onServiceConnected() {
        updatePlayPauseDrawableState()
        updateRepeatState()
        updateShuffleState()
        updateSong()
    }

    override fun onPlayingMetaChanged() {
        super.onPlayingMetaChanged()
        updateSong()
    }

    override fun onPlayStateChanged() {
        updatePlayPauseDrawableState()
    }

    override fun onRepeatModeChanged() {
        updateRepeatState()
    }

    override fun onShuffleModeChanged() {
        updateShuffleState()
    }

    override fun setDark(color: Int) {

        val colorBg = ATHUtil.resolveColor(requireContext(), android.R.attr.colorBackground)
        if (ColorUtil.isColorLight(colorBg)) {
            lastPlaybackControlsColor = MaterialValueHelper.getSecondaryTextColor(requireContext(), true)
            lastDisabledPlaybackControlsColor = MaterialValueHelper.getSecondaryDisabledTextColor(requireContext(), true)
        } else {
            lastPlaybackControlsColor = MaterialValueHelper.getPrimaryTextColor(requireContext(), false)
            lastDisabledPlaybackControlsColor = MaterialValueHelper.getPrimaryDisabledTextColor(requireContext(), false)
        }

        val colorFinal = if (PreferenceUtil.getInstance(requireContext()).adaptiveColor) {
            color
        } else {
            ThemeStore.textColorSecondary(requireContext())
        }.ripAlpha()

        volumeFragment?.setTintable(colorFinal)
        ViewUtil.setProgressDrawable(progressSlider, colorFinal, true)

        updatePrevNextColor()

        val isDark = ColorUtil.isColorLight(colorFinal)
        text.setTextColor(colorFinal)

        TintHelper.setTintAuto(playPauseButton, MaterialValueHelper.getPrimaryTextColor(requireContext(), isDark), false)
        TintHelper.setTintAuto(playPauseButton, colorFinal, true)
    }

    private fun setUpPlayPauseFab() {
        playPauseButton.setOnClickListener(PlayPauseButtonOnClickHandler())
    }

    private fun updatePlayPauseDrawableState() {
        if (MusicPlayerRemote.isPlaying) {
            playPauseButton.setImageResource(R.drawable.ic_pause_white_24dp)
        } else {
            playPauseButton.setImageResource(R.drawable.ic_play_arrow_white_32dp)
        }
    }


    private fun setUpMusicControllers() {
        setUpPlayPauseFab()
        setUpPrevNext()
        setUpProgressSlider()
        setUpShuffleButton()
        setUpRepeatButton()
    }

    private fun setUpPrevNext() {
        updatePrevNextColor()
        nextButton.setOnClickListener { MusicPlayerRemote.playNextSong() }
        previousButton.setOnClickListener { MusicPlayerRemote.back() }
    }

    private fun updatePrevNextColor() {
        nextButton.setColorFilter(lastPlaybackControlsColor, PorterDuff.Mode.SRC_IN)
        previousButton.setColorFilter(lastPlaybackControlsColor, PorterDuff.Mode.SRC_IN)
    }

    private fun setUpShuffleButton() {
        shuffleButton.setOnClickListener { MusicPlayerRemote.toggleShuffleMode() }
    }

    override fun updateShuffleState() {
        when (MusicPlayerRemote.shuffleMode) {
            MusicService.SHUFFLE_MODE_SHUFFLE -> shuffleButton.setColorFilter(lastPlaybackControlsColor, PorterDuff.Mode.SRC_IN)
            else -> shuffleButton.setColorFilter(lastDisabledPlaybackControlsColor, PorterDuff.Mode.SRC_IN)
        }
    }

    private fun setUpRepeatButton() {
        repeatButton.setOnClickListener { MusicPlayerRemote.cycleRepeatMode() }
    }

    override fun updateRepeatState() {
        when (MusicPlayerRemote.repeatMode) {
            MusicService.REPEAT_MODE_NONE -> {
                repeatButton.setImageResource(R.drawable.ic_repeat_white_24dp)
                repeatButton.setColorFilter(lastDisabledPlaybackControlsColor, PorterDuff.Mode.SRC_IN)
            }
            MusicService.REPEAT_MODE_ALL -> {
                repeatButton.setImageResource(R.drawable.ic_repeat_white_24dp)
                repeatButton.setColorFilter(lastPlaybackControlsColor, PorterDuff.Mode.SRC_IN)
            }
            MusicService.REPEAT_MODE_THIS -> {
                repeatButton.setImageResource(R.drawable.ic_repeat_one_white_24dp)
                repeatButton.setColorFilter(lastPlaybackControlsColor, PorterDuff.Mode.SRC_IN)
            }
        }
    }

    public override fun show() {
        playPauseButton!!.animate()
                .scaleX(1f)
                .scaleY(1f)
                .rotation(360f)
                .setInterpolator(DecelerateInterpolator())
                .start()
    }

    public override fun hide() {
        if (playPauseButton != null) {
            playPauseButton!!.apply {
                scaleX = 0f
                scaleY = 0f
                rotation = 0f
            }
        }
    }

    override fun setUpProgressSlider() {
        progressSlider.setOnSeekBarChangeListener(object : SimpleOnSeekbarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    MusicPlayerRemote.seekTo(progress)
                    onUpdateProgressViews(MusicPlayerRemote.songProgressMillis,
                            MusicPlayerRemote.songDurationMillis)
                }
            }
        })
    }

    override fun onUpdateProgressViews(progress: Int, total: Int) {
        progressSlider.max = total

        val animator = ObjectAnimator.ofInt(progressSlider, "progress", progress)
        animator.duration = SLIDER_ANIMATION_TIME
        animator.interpolator = LinearInterpolator()
        animator.start()

        songTotalTime.text = MusicUtil.getReadableDurationString(total.toLong())
        songCurrentProgress.text = MusicUtil.getReadableDurationString(progress.toLong())
    }
}