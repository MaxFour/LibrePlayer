package com.maxfour.music.fragments.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.maxfour.music.R
import com.maxfour.music.adapter.album.AlbumCoverPagerAdapter
import com.maxfour.music.adapter.album.AlbumCoverPagerAdapter.AlbumCoverFragment
import com.maxfour.music.fragments.NowPlayingScreen
import com.maxfour.music.fragments.base.AbsMusicServiceFragment
import com.maxfour.music.helper.MusicPlayerRemote
import com.maxfour.music.transform.CarousalPagerTransformer
import com.maxfour.music.transform.ParallaxPagerTransformer
import com.maxfour.music.util.PreferenceUtil
import kotlinx.android.synthetic.main.fragment_player_album_cover.*


class PlayerAlbumCoverFragment : AbsMusicServiceFragment(), ViewPager.OnPageChangeListener {


    private var callbacks: Callbacks? = null
    private var currentPosition: Int = 0
    private val colorReceiver = object : AlbumCoverFragment.ColorReceiver {
        override fun onColorReady(color: Int, request: Int) {
            if (currentPosition == request) {
                notifyColorChange(color)
            }
        }
    }

    fun removeSlideEffect() {
        val transformer = ParallaxPagerTransformer(R.id.player_image)
        transformer.setSpeed(0.3f)
        viewPager.setPageTransformer(true, transformer)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_player_album_cover, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewPager.addOnPageChangeListener(this)
        //noinspection ConstantConditions
        if (PreferenceUtil.getInstance(requireContext()).carouselEffect() &&
                !((PreferenceUtil.getInstance(requireContext()).nowPlayingScreen == NowPlayingScreen.FULL) ||
                        (PreferenceUtil.getInstance(requireContext()).nowPlayingScreen == NowPlayingScreen.ADAPTIVE)
                        || (PreferenceUtil.getInstance(requireContext()).nowPlayingScreen == NowPlayingScreen.FIT))) {
            viewPager.clipToPadding = false
            viewPager.setPadding(40, 40, 40, 0)
            viewPager.pageMargin = 0
            viewPager.setPageTransformer(false, CarousalPagerTransformer(requireContext()))
        } else {
            viewPager.offscreenPageLimit = 2
            viewPager.setPageTransformer(true, PreferenceUtil.getInstance(requireContext()).albumCoverTransform)
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewPager.removeOnPageChangeListener(this)
    }

    override fun onServiceConnected() {
        updatePlayingQueue()
    }

    override fun onPlayingMetaChanged() {
        viewPager.currentItem = MusicPlayerRemote.position
    }

    override fun onQueueChanged() {
        updatePlayingQueue()
    }

    private fun updatePlayingQueue() {
        viewPager.apply {
            adapter = AlbumCoverPagerAdapter(fragmentManager!!, MusicPlayerRemote.playingQueue)
            viewPager.adapter!!.notifyDataSetChanged()
            viewPager.currentItem = MusicPlayerRemote.position
            onPageSelected(MusicPlayerRemote.position)
        }
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        currentPosition = position
        if (viewPager.adapter != null) {
            (viewPager.adapter as AlbumCoverPagerAdapter).receiveColor(colorReceiver, position)
        }
        if (position != MusicPlayerRemote.position) {
            MusicPlayerRemote.playSongAt(position)
        }
    }

    override fun onPageScrollStateChanged(state: Int) {

    }


    private fun notifyColorChange(color: Int) {
        if (callbacks != null) {
            callbacks!!.onColorChanged(color)
        }
    }

    fun setCallbacks(listener: Callbacks) {
        callbacks = listener
    }

    fun removeEffect() {
        viewPager.setPageTransformer(false, null)
    }


    interface Callbacks {

        fun onColorChanged(color: Int)

        fun onFavoriteToggled()

    }


    companion object {
        val TAG: String = PlayerAlbumCoverFragment::class.java.simpleName
    }
}
