package com.maxfour.libreplayer.fragments.player.full

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.PopupMenu
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import com.maxfour.appthemehelper.ThemeStore
import com.maxfour.appthemehelper.util.ColorUtil
import com.maxfour.appthemehelper.util.MaterialValueHelper
import com.maxfour.appthemehelper.util.TintHelper
import com.maxfour.libreplayer.R
import com.maxfour.libreplayer.extensions.ripAlpha
import com.maxfour.libreplayer.fragments.base.AbsPlayerControlsFragment
import com.maxfour.libreplayer.helper.MusicPlayerRemote
import com.maxfour.libreplayer.helper.MusicProgressViewUpdateHelper
import com.maxfour.libreplayer.helper.PlayPauseButtonOnClickHandler
import com.maxfour.libreplayer.misc.SimpleOnSeekbarChangeListener
import com.maxfour.libreplayer.model.Song
import com.maxfour.libreplayer.service.MusicService
import com.maxfour.libreplayer.util.MusicUtil
import com.maxfour.libreplayer.util.PreferenceUtil
import com.maxfour.libreplayer.util.ViewUtil
import kotlinx.android.synthetic.main.fragment_full_player_controls.*

class FullPlaybackControlsFragment : AbsPlayerControlsFragment(), PopupMenu.OnMenuItemClickListener {

    private var lastPlaybackControlsColor: Int = 0
    private var lastDisabledPlaybackControlsColor: Int = 0
    private var progressViewUpdateHelper: MusicProgressViewUpdateHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressViewUpdateHelper = MusicProgressViewUpdateHelper(this)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_full_player_controls, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpMusicControllers()

        songTotalTime.setTextColor(Color.WHITE)
        songCurrentProgress.setTextColor(Color.WHITE)

        title.isSelected = true
    }

    override fun onResume() {
        super.onResume()
        progressViewUpdateHelper!!.start()
    }

    override fun onPause() {
        super.onPause()
        progressViewUpdateHelper!!.stop()
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

    public override fun show() {
        playPauseButton!!.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setInterpolator(DecelerateInterpolator())
                .start()
    }


    public override fun hide() {
        playPauseButton.apply {
            scaleX = 0f
            scaleY = 0f
            rotation = 0f
        }
    }

    override fun setDark(color: Int) {
        lastPlaybackControlsColor = Color.WHITE
        lastDisabledPlaybackControlsColor = ContextCompat.getColor(context!!, R.color.md_grey_500)

        val colorFinal = if (PreferenceUtil.getInstance(requireContext()).adaptiveColor) {
            color
        } else {
            ThemeStore.accentColor(context!!).ripAlpha()
        }
        volumeFragment?.setTintableColor(colorFinal)
        text.setTextColor(colorFinal)

        ViewUtil.setProgressDrawable(progressSlider, colorFinal, true)

        playPauseButton.backgroundTintList = ColorStateList.valueOf(colorFinal)
        playPauseButton.imageTintList = ColorStateList.valueOf(MaterialValueHelper.getPrimaryTextColor(context, ColorUtil.isColorLight(colorFinal)))

        updateRepeatState()
        updateShuffleState()
        updatePrevNextColor()

    }

    override fun onServiceConnected() {
        updatePlayPauseDrawableState()
        updateRepeatState()
        updateShuffleState()
        updateSong()
    }

    private fun updateSong() {
        val song = MusicPlayerRemote.currentSong
        title.text = song.title
        text.text = song.artistName
        updateIsFavorite()
    }

    override fun onPlayingMetaChanged() {
        super.onPlayingMetaChanged()
        updateSong()
    }

    override fun onPlayStateChanged() {
        updatePlayPauseDrawableState()
    }

    private fun updatePlayPauseDrawableState() {
        if (MusicPlayerRemote.isPlaying) {
            playPauseButton.setImageResource(R.drawable.ic_pause_white_24dp)
        } else {
            playPauseButton.setImageResource(R.drawable.ic_play_arrow_white_32dp)
        }
    }

    private fun setUpPlayPauseFab() {

        playPauseButton.setOnClickListener(PlayPauseButtonOnClickHandler())
        playPauseButton.post {
            if (playPauseButton != null) {
                playPauseButton.pivotX = (playPauseButton.width / 2).toFloat()
                playPauseButton.pivotY = (playPauseButton.height / 2).toFloat()
            }
        }
    }

    private fun setUpMusicControllers() {
        setUpPlayPauseFab()
        setUpPrevNext()
        setUpRepeatButton()
        setUpShuffleButton()
        setUpProgressSlider()
        setupFavourite()
        setupMenu()
    }

    private fun setupMenu() {
        playerMenu.setOnClickListener {
            val popupMenu = PopupMenu(context!!, it)
            popupMenu.setOnMenuItemClickListener(this)
            popupMenu.inflate(R.menu.menu_player)
            popupMenu.show()
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return (parentFragment as FullPlayerFragment).onMenuItemClick(item!!)
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

    override fun setUpProgressSlider() {
        progressSlider!!.setOnSeekBarChangeListener(object : SimpleOnSeekbarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    MusicPlayerRemote.seekTo(progress)
                    onUpdateProgressViews(MusicPlayerRemote.songProgressMillis, MusicPlayerRemote.songDurationMillis)
                }
            }
        })
    }


    override fun onRepeatModeChanged() {
        updateRepeatState()
    }

    override fun onShuffleModeChanged() {
        updateShuffleState()
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

    private fun setupFavourite() {
        songFavourite?.setOnClickListener {
            toggleFavorite(MusicPlayerRemote.currentSong)
        }
    }

    private fun toggleFavorite(song: Song) {
        MusicUtil.toggleFavorite(activity!!, song)
        if (song.id == MusicPlayerRemote.currentSong.id) {
            updateIsFavorite()
        }
    }

    private var updateIsFavoriteTask: AsyncTask<*, *, *>? = null

    @SuppressLint("StaticFieldLeak")
    fun updateIsFavorite() {
        if (updateIsFavoriteTask != null) {
            updateIsFavoriteTask!!.cancel(false)
        }
        updateIsFavoriteTask = object : AsyncTask<Song, Void, Boolean>() {
            override fun doInBackground(vararg params: Song): Boolean? {
                val activity = activity
                return if (activity != null) {
                    MusicUtil.isFavorite(requireActivity(), params[0])
                } else {
                    cancel(false)
                    null
                }
            }

            override fun onPostExecute(isFavorite: Boolean?) {
                val activity = activity
                if (activity != null) {
                    val res = if (isFavorite!!)
                        R.drawable.ic_favorite_white_24dp
                    else
                        R.drawable.ic_favorite_border_white_24dp

                    val drawable = TintHelper.createTintedDrawable(activity, res, Color.WHITE)
                    songFavourite?.setImageDrawable(drawable)
                }
            }
        }.execute(MusicPlayerRemote.currentSong)
    }

    fun onFavoriteToggled() {
        toggleFavorite(MusicPlayerRemote.currentSong)
    }

}
