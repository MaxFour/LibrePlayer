package com.maxfour.libreplayer.fragments.mainactivity.home

import android.app.ActivityOptions
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.maxfour.appthemehelper.common.ATHToolbarActivity
import com.maxfour.appthemehelper.util.ATHUtil
import com.maxfour.appthemehelper.util.ColorUtil
import com.maxfour.appthemehelper.util.ToolbarContentTintHelper
import com.maxfour.libreplayer.App
import com.maxfour.libreplayer.Constants
import com.maxfour.libreplayer.Constants.USER_BANNER
import com.maxfour.libreplayer.R
import com.maxfour.libreplayer.adapter.HomeAdapter
import com.maxfour.libreplayer.dialogs.OptionsSheetDialogFragment
import com.maxfour.libreplayer.extensions.show
import com.maxfour.libreplayer.fragments.base.AbsMainActivityFragment
import com.maxfour.libreplayer.helper.MusicPlayerRemote
import com.maxfour.libreplayer.interfaces.MainActivityFragmentCallbacks
import com.maxfour.libreplayer.loaders.SongLoader
import com.maxfour.libreplayer.model.Home
import com.maxfour.libreplayer.model.smartplaylist.HistoryPlaylist
import com.maxfour.libreplayer.model.smartplaylist.LastAddedPlaylist
import com.maxfour.libreplayer.model.smartplaylist.MyTopSongsPlaylist
import com.maxfour.libreplayer.mvp.presenter.HomePresenter
import com.maxfour.libreplayer.mvp.presenter.HomeView
import com.maxfour.libreplayer.util.NavigationUtil
import com.maxfour.libreplayer.util.PlayerColorUtil
import com.maxfour.libreplayer.util.PreferenceUtil
import kotlinx.android.synthetic.main.abs_playlists.*
import kotlinx.android.synthetic.main.fragment_banner_home.*
import kotlinx.android.synthetic.main.home_content.*
import java.io.File
import java.util.*
import javax.inject.Inject

class BannerHomeFragment : AbsMainActivityFragment(), MainActivityFragmentCallbacks, HomeView {
    @Inject
    lateinit var homePresenter: HomePresenter

    private lateinit var homeAdapter: HomeAdapter
    private lateinit var toolbar: Toolbar

    override fun sections(sections: ArrayList<Home>) {
        println(sections.size)
        homeAdapter.swapData(sections)
    }

    override fun onCreateView(inflater: LayoutInflater, viewGroup: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(if (PreferenceUtil.getInstance(requireContext()).isHomeBanner) R.layout.fragment_banner_home else R.layout.fragment_home, viewGroup, false)
    }

    private fun loadImageFromStorage() {
        Glide.with(requireContext())
                .load(File(PreferenceUtil.getInstance(requireContext()).profileImage, Constants.USER_PROFILE))
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .placeholder(R.drawable.ic_person_flat)
                .error(R.drawable.ic_person_flat)
                .into(userImage)
    }

    private val displayMetrics: DisplayMetrics
        get() {
            val display = mainActivity.windowManager.defaultDisplay
            val metrics = DisplayMetrics()
            display.getMetrics(metrics)
            return metrics
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStatusBarColorAuto(view)
        toolbar = view.findViewById(R.id.toolbar)

        bannerImage?.setOnClickListener {
            val options = ActivityOptions.makeSceneTransitionAnimation(mainActivity, userImage, getString(R.string.transition_user_image))
            NavigationUtil.goToUserInfo(requireActivity(), options)
        }

        lastAdded.setOnClickListener {
            NavigationUtil.goToPlaylistNew(requireActivity(), LastAddedPlaylist(requireActivity()))
        }

        topPlayed.setOnClickListener {
            NavigationUtil.goToPlaylistNew(requireActivity(), MyTopSongsPlaylist(requireActivity()))
        }

        actionShuffle.setOnClickListener {
            MusicPlayerRemote.openAndShuffleQueue(SongLoader.getAllSongs(requireActivity()), true)
        }

        history.setOnClickListener {
            NavigationUtil.goToPlaylistNew(requireActivity(), HistoryPlaylist(requireActivity()))
        }

        setupToolbar()

        userImage?.setOnClickListener {
            val options = ActivityOptions.makeSceneTransitionAnimation(mainActivity, userImage, getString(R.string.transition_user_image))
            NavigationUtil.goToUserInfo(requireActivity(), options)
        }
        titleWelcome?.text = String.format("%s", PreferenceUtil.getInstance(requireContext()).userName)

        App.musicComponent.inject(this)
        homeAdapter = HomeAdapter(mainActivity, displayMetrics)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(mainActivity)
            adapter = homeAdapter
        }
        homePresenter.attachView(this)
        homePresenter.loadSections()
    }

    private fun toolbarColor(): Int {
        return if (PreferenceUtil.getInstance(requireContext()).isHomeBanner) {
            ColorUtil.withAlpha(PlayerColorUtil.toolbarColor(mainActivity), 0.85f)
        } else {
            PlayerColorUtil.toolbarColor(mainActivity)
        }
    }

    private fun setupToolbar() {
        toolbar.apply {
            backgroundTintList = ColorStateList.valueOf(ATHUtil.resolveColor(requireContext(), R.attr.colorSurface))
            setNavigationIcon(R.drawable.ic_menu_white_24dp)
            setOnClickListener {
                val options = ActivityOptions.makeSceneTransitionAnimation(mainActivity, toolbarContainer, getString(R.string.transition_toolbar))
                NavigationUtil.goToSearch(requireActivity(), options)
            }

        }
        mainActivity.setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { showMainMenu(OptionsSheetDialogFragment.LIBRARY) }
    }

    override fun handleBackPress(): Boolean {
        return false
    }

    override fun onResume() {
        super.onResume()
        getTimeOfTheDay()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        homePresenter.detachView()
    }

    override fun showEmptyView() {
        emptyContainer.show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_search, menu)

        ToolbarContentTintHelper.handleOnCreateOptionsMenu(requireActivity(), toolbar, menu, ATHToolbarActivity.getToolbarBackgroundColor(toolbar))
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        ToolbarContentTintHelper.handleOnPrepareOptionsMenu(requireActivity(), toolbar)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_search) {
            val options = ActivityOptions.makeSceneTransitionAnimation(mainActivity, toolbarContainer, getString(R.string.transition_toolbar))
            NavigationUtil.goToSearch(requireActivity(), true, options)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getTimeOfTheDay() {
        val c = Calendar.getInstance()
        val timeOfDay = c.get(Calendar.HOUR_OF_DAY)
        var images = arrayOf<String>()
        when (timeOfDay) {
            in 0..5 -> images = resources.getStringArray(R.array.night)
            in 6..11 -> images = resources.getStringArray(R.array.morning)
            in 12..15 -> images = resources.getStringArray(R.array.after_noon)
            in 16..19 -> images = resources.getStringArray(R.array.evening)
            in 20..23 -> images = resources.getStringArray(R.array.night)
        }
        val day = images[Random().nextInt(images.size)]
        loadTimeImage(day)
    }


    private fun loadTimeImage(day: String) {
        bannerImage?.let {
            val request = Glide.with(requireContext())
            if (PreferenceUtil.getInstance(requireContext()).bannerImage.isEmpty()) {
                request.load(day)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .placeholder(R.drawable.material_design_default)
                        .error(R.drawable.material_design_default)
                        .into(it)
            } else {
                request.load(File(PreferenceUtil.getInstance(requireContext()).bannerImage, USER_BANNER))
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .placeholder(R.drawable.material_design_default)
                        .error(R.drawable.material_design_default)
                        .into(it)
            }
        }
        loadImageFromStorage()
    }

    companion object {

        const val TAG: String = "BannerHomeFragment"

        fun newInstance(): BannerHomeFragment {
            return BannerHomeFragment()
        }
    }
}
