package com.maxfour.libreplayer.fragments.player.cardblur

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.maxfour.appthemehelper.util.ToolbarContentTintHelper
import com.maxfour.libreplayer.R
import com.maxfour.libreplayer.fragments.base.AbsPlayerFragment
import com.maxfour.libreplayer.fragments.player.PlayerAlbumCoverFragment
import com.maxfour.libreplayer.fragments.player.normal.PlayerFragment
import com.maxfour.libreplayer.glide.BlurTransformation
import com.maxfour.libreplayer.glide.PlayerColoredTarget
import com.maxfour.libreplayer.glide.SongGlideRequest
import com.maxfour.libreplayer.helper.MusicPlayerRemote
import com.maxfour.libreplayer.model.Song
import com.maxfour.libreplayer.util.PreferenceUtil
import kotlinx.android.synthetic.main.fragment_card_blur_player.*

class CardBlurFragment : AbsPlayerFragment(), SharedPreferences.OnSharedPreferenceChangeListener {
    override fun playerToolbar(): Toolbar {
        return playerToolbar
    }

    private var lastColor: Int = 0
    override val paletteColor: Int
        get() = lastColor
    private lateinit var playbackControlsFragment: CardBlurPlaybackControlsFragment


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
        return Color.WHITE
    }

    override fun onColorChanged(color: Int) {
        playbackControlsFragment.setDark(color)
        lastColor = color
        callbacks!!.onPaletteColorChanged()
        ToolbarContentTintHelper.colorizeToolbar(playerToolbar, Color.WHITE, activity)

        playerToolbar.setTitleTextColor(Color.WHITE)
        playerToolbar.setSubtitleTextColor(Color.WHITE)
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

        return inflater.inflate(R.layout.fragment_card_blur_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpSubFragments()
        setUpPlayerToolbar()
    }

    private fun setUpSubFragments() {
        playbackControlsFragment = childFragmentManager.findFragmentById(R.id.playbackControlsFragment) as CardBlurPlaybackControlsFragment
        val playerAlbumCoverFragment = childFragmentManager.findFragmentById(R.id.playerAlbumCoverFragment) as PlayerAlbumCoverFragment?
        if (playerAlbumCoverFragment != null) {
            playerAlbumCoverFragment.setCallbacks(this)
            playerAlbumCoverFragment.removeEffect()
        }

    }

    private fun setUpPlayerToolbar() {
        playerToolbar.apply {
            inflateMenu(R.menu.menu_player)
            setNavigationOnClickListener { activity!!.onBackPressed() }
            setTitleTextColor(Color.WHITE)
            setSubtitleTextColor(Color.WHITE)
            ToolbarContentTintHelper.colorizeToolbar(playerToolbar, Color.WHITE, activity)
        }.setOnMenuItemClickListener(this)
    }

    override fun onServiceConnected() {
        updateIsFavorite()
        updateBlur()
        updateSong()
    }

    override fun onPlayingMetaChanged() {
        updateIsFavorite()
        updateBlur()
        updateSong()
    }

    private fun updateSong() {
        val song = MusicPlayerRemote.currentSong
        playerToolbar.apply {
            title = song.title
            subtitle = song.artistName
        }
    }

    private fun updateBlur() {
        val blurAmount = PreferenceManager.getDefaultSharedPreferences(requireContext())
                .getInt(PreferenceUtil.NEW_BLUR_AMOUNT, 25)
        colorBackground!!.clearColorFilter()
        SongGlideRequest.Builder.from(Glide.with(requireActivity()), MusicPlayerRemote.currentSong)
                .checkIgnoreMediaStore(requireContext())
                .generatePalette(requireContext()).build()
                .transform(BlurTransformation.Builder(requireContext()).blurRadius(blurAmount.toFloat()).build())
                //.centerCrop()
                //.override(320, 480)
                .into(object : PlayerColoredTarget(colorBackground) {
                    override fun onColorReady(color: Int) {
                        if (color == defaultFooterColor) {
                            colorBackground!!.setColorFilter(color)
                        }
                    }
                })
    }

    override fun onResume() {
        super.onResume()
        PreferenceManager.getDefaultSharedPreferences(requireContext()).registerOnSharedPreferenceChangeListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        PreferenceManager.getDefaultSharedPreferences(requireContext()).unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == PreferenceUtil.NEW_BLUR_AMOUNT) {
            updateBlur()
        }
    }

    companion object {
        fun newInstance(): PlayerFragment {
            return PlayerFragment()
        }
    }
}
