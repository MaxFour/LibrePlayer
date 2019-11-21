package com.maxfour.music.fragments.player.peak

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.maxfour.appthemehelper.util.ATHUtil
import com.maxfour.appthemehelper.util.ToolbarContentTintHelper
import com.maxfour.music.R
import com.maxfour.music.fragments.base.AbsPlayerFragment
import com.maxfour.music.glide.MusicPlayerColoredTarget
import com.maxfour.music.glide.SongGlideRequest
import com.maxfour.music.helper.MusicPlayerRemote
import com.maxfour.music.util.NavigationUtil
import kotlinx.android.synthetic.main.fragment_peak_player.*

class PeakPlayerFragment : AbsPlayerFragment() {

    private lateinit var playbackControlsFragment: PeakPlayerControlFragment
    private var lastColor: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_peak_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpPlayerToolbar()
        setUpSubFragments()
        title.isSelected = true
        playerImage.setOnClickListener {
            NavigationUtil.goToLyrics(requireActivity())
        }
    }

    private fun setUpSubFragments() {
        playbackControlsFragment = childFragmentManager.findFragmentById(R.id.playbackControlsFragment) as PeakPlayerControlFragment
    }

    private fun setUpPlayerToolbar() {
        playerToolbar.apply {
            inflateMenu(R.menu.menu_player)
            setNavigationOnClickListener { activity!!.onBackPressed() }
            setOnMenuItemClickListener(this@PeakPlayerFragment)
            ToolbarContentTintHelper.colorizeToolbar(this, ATHUtil.resolveColor(context, R.attr.iconColor), activity)
        }
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
        return ATHUtil.resolveColor(requireContext(), R.attr.iconColor)
    }

    override val paletteColor: Int
        get() = lastColor

    override fun onColorChanged(color: Int) {
        playbackControlsFragment.setDark(color)
        lastColor = color
        callbacks?.onPaletteColorChanged()
    }

    override fun onFavoriteToggled() {

    }

    private fun updateSong() {
        val song = MusicPlayerRemote.currentSong
        title.text = song.title
        text.text = song.artistName

        SongGlideRequest.Builder.from(Glide.with(requireActivity()), MusicPlayerRemote.currentSong)
                .checkIgnoreMediaStore(requireContext())
                .generatePalette(requireContext())
                .build()
                .into(object : MusicPlayerColoredTarget(playerImage) {
                    override fun onColorReady(color: Int) {
                        playbackControlsFragment.setDark(color)
                    }
                })

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