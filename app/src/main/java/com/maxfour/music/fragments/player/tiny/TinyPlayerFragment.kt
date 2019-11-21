package com.maxfour.music.fragments.player.tiny

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.appcompat.widget.Toolbar
import com.maxfour.appthemehelper.ThemeStore
import com.maxfour.appthemehelper.util.ColorUtil
import com.maxfour.appthemehelper.util.MaterialValueHelper
import com.maxfour.appthemehelper.util.ToolbarContentTintHelper
import com.maxfour.music.R
import com.maxfour.music.fragments.MiniPlayerFragment
import com.maxfour.music.fragments.base.AbsPlayerFragment
import com.maxfour.music.fragments.player.PlayerAlbumCoverFragment
import com.maxfour.music.helper.MusicPlayerRemote
import com.maxfour.music.helper.MusicProgressViewUpdateHelper
import com.maxfour.music.helper.PlayPauseButtonOnClickHandler
import com.maxfour.music.model.Song
import com.maxfour.music.util.MusicUtil
import com.maxfour.music.util.PreferenceUtil
import com.maxfour.music.util.ViewUtil
import kotlinx.android.synthetic.main.fragment_tiny_player.*

class TinyPlayerFragment : AbsPlayerFragment(), MusicProgressViewUpdateHelper.Callback {
    override fun onUpdateProgressViews(progress: Int, total: Int) {
        progressBar.max = total

        val animator = ObjectAnimator.ofInt(progressBar, "progress", progress)

        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(animator)

        animatorSet.duration = 1500
        animatorSet.interpolator = LinearInterpolator()
        animatorSet.start()

        playerSongTotalTime.text = String.format("%s/%s", MusicUtil.getReadableDurationString(total.toLong()),
                MusicUtil.getReadableDurationString(progress.toLong()))
    }


    override fun playerToolbar(): Toolbar {
        return playerToolbar
    }

    override fun onShow() {

    }

    override fun onHide() {
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    override fun toolbarIconColor(): Int {
        return textColorPrimary
    }

    private var lastColor: Int = 0
    override val paletteColor: Int
        get() = lastColor

    private var textColorPrimary = 0
    private var textColorPrimaryDisabled = 0

    override fun onColorChanged(color: Int) {

        val colorFinal = if (PreferenceUtil.getInstance(requireContext()).adaptiveColor) {
            color
        } else {
            ThemeStore.accentColor(requireContext())
        }

        if (ColorUtil.isColorLight(colorFinal)) {
            textColorPrimary = MaterialValueHelper.getSecondaryTextColor(requireContext(), true)
            textColorPrimaryDisabled = MaterialValueHelper.getSecondaryTextColor(requireContext(), true)
        } else {
            textColorPrimary = MaterialValueHelper.getPrimaryTextColor(requireContext(), false)
            textColorPrimaryDisabled = MaterialValueHelper.getSecondaryTextColor(requireContext(), false)
        }

        this.lastColor = colorFinal

        callbacks?.onPaletteColorChanged()

        tinyPlaybackControlsFragment.setDark(colorFinal)

        ViewUtil.setProgressDrawable(progressBar, colorFinal)

        title.setTextColor(textColorPrimary)
        text.setTextColor(textColorPrimaryDisabled)

        playerSongTotalTime.setTextColor(textColorPrimary)

        ToolbarContentTintHelper.colorizeToolbar(playerToolbar, textColorPrimary, requireActivity())
    }

    override fun onFavoriteToggled() {
        toggleFavorite(MusicPlayerRemote.currentSong)
    }

    private lateinit var tinyPlaybackControlsFragment: TinyPlaybackControlsFragment
    private lateinit var progressViewUpdateHelper: MusicProgressViewUpdateHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressViewUpdateHelper = MusicProgressViewUpdateHelper(this)
    }

    override fun onResume() {
        super.onResume()
        progressViewUpdateHelper.start()
    }

    override fun onPause() {
        super.onPause()
        progressViewUpdateHelper.stop()
    }

    private fun updateSong() {
        val song = MusicPlayerRemote.currentSong
        title.text = song.title
        text.text = String.format("%s \nby - %s", song.albumName, song.artistName)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tiny_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        title.isSelected = true
        progressBar.setOnClickListener(PlayPauseButtonOnClickHandler())
        progressBar.setOnTouchListener(MiniPlayerFragment.FlingPlayBackController(activity!!))

        setUpPlayerToolbar()
        setUpSubFragments()
    }

    private fun setUpSubFragments() {
        tinyPlaybackControlsFragment = childFragmentManager.findFragmentById(R.id.playbackControlsFragment) as TinyPlaybackControlsFragment
        val playerAlbumCoverFragment = childFragmentManager.findFragmentById(R.id.playerAlbumCoverFragment) as PlayerAlbumCoverFragment
        playerAlbumCoverFragment.setCallbacks(this)

    }

    private fun setUpPlayerToolbar() {
        playerToolbar.apply {
            inflateMenu(R.menu.menu_player)
            setNavigationOnClickListener { activity!!.onBackPressed() }
            setOnMenuItemClickListener(this@TinyPlayerFragment)
        }
    }

    override fun toggleFavorite(song: Song) {
        super.toggleFavorite(song)
        if (song.id == MusicPlayerRemote.currentSong.id) {
            updateIsFavorite()
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        updateSong()
    }

    override fun onPlayingMetaChanged() {
        super.onPlayingMetaChanged()
        updateSong()
    }

}