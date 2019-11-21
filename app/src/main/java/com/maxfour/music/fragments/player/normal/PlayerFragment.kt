package com.maxfour.music.fragments.player.normal

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import com.maxfour.appthemehelper.util.ATHUtil
import com.maxfour.appthemehelper.util.ToolbarContentTintHelper
import com.maxfour.music.R
import com.maxfour.music.fragments.base.AbsPlayerFragment
import com.maxfour.music.fragments.player.PlayerAlbumCoverFragment
import com.maxfour.music.helper.MusicPlayerRemote
import com.maxfour.music.model.Song
import com.maxfour.music.util.PreferenceUtil
import com.maxfour.music.util.ViewUtil
import com.maxfour.music.views.DrawableGradient
import kotlinx.android.synthetic.main.fragment_player.*


class PlayerFragment : AbsPlayerFragment()  {

    private var lastColor: Int = 0
    override val paletteColor: Int
        get() = lastColor

    private lateinit var playbackControlsFragment: PlayerPlaybackControlsFragment
    private var valueAnimator: ValueAnimator? = null


    private fun colorize(i: Int) {
        if (valueAnimator != null) {
            valueAnimator!!.cancel()
        }

        valueAnimator = ValueAnimator.ofObject(ArgbEvaluator(), android.R.color.transparent, i)
        valueAnimator!!.addUpdateListener { animation ->
            val drawable = DrawableGradient(GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(animation.animatedValue as Int, android.R.color.transparent), 0)
            colorGradientBackground?.background = drawable
        }
        valueAnimator!!.setDuration(ViewUtil.MUSIC_ANIM_TIME.toLong()).start()
    }

    override fun onShow() {
        playbackControlsFragment.show()
    }

    override fun onHide() {
        playbackControlsFragment.hide()
        onBackPressed()
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    override fun toolbarIconColor(): Int {
        return ATHUtil.resolveColor(context, R.attr.iconColor)
    }

    override fun onColorChanged(color: Int) {
        playbackControlsFragment.setDark(color)
        lastColor = color
        callbacks!!.onPaletteColorChanged()

        ToolbarContentTintHelper.colorizeToolbar(playerToolbar, ATHUtil.resolveColor(context, R.attr.iconColor), activity)

        if (PreferenceUtil.getInstance(requireContext()).adaptiveColor) {
            colorize(color)
        }
    }

    private fun getCutOff(): Int {
        return if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) 20 else 0
    }

    override fun toggleFavorite(song: Song) {
        super.toggleFavorite(song)
        if (song.id == MusicPlayerRemote.currentSong.id) {
            updateIsFavorite()
        }
    }

    override fun onFavoriteToggled() {
        toggleFavorite(MusicPlayerRemote.currentSong)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpSubFragments()
        setUpPlayerToolbar()
    }


    private fun setUpSubFragments() {
        playbackControlsFragment = childFragmentManager.findFragmentById(R.id.playbackControlsFragment) as PlayerPlaybackControlsFragment
        val playerAlbumCoverFragment = childFragmentManager.findFragmentById(R.id.playerAlbumCoverFragment) as PlayerAlbumCoverFragment
        playerAlbumCoverFragment.setCallbacks(this)
    }

    private fun setUpPlayerToolbar() {
        playerToolbar.inflateMenu(R.menu.menu_player)
        playerToolbar.setNavigationOnClickListener { activity!!.onBackPressed() }
        playerToolbar.setOnMenuItemClickListener(this)

        ToolbarContentTintHelper.colorizeToolbar(playerToolbar, ATHUtil.resolveColor(context, R.attr.iconColor), activity)
    }

    override fun onServiceConnected() {
        updateIsFavorite()
    }

    override fun onPlayingMetaChanged() {
        updateIsFavorite()
    }

    override fun playerToolbar(): Toolbar {
        return playerToolbar
    }

    companion object {

        fun newInstance(): PlayerFragment {
            return PlayerFragment()
        }
    }
}

