package com.maxfour.libreplayer.fragments.base

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.maxfour.appthemehelper.util.ATHUtil
import com.maxfour.libreplayer.R
import com.maxfour.libreplayer.activities.tageditor.AbsTagEditorActivity
import com.maxfour.libreplayer.activities.tageditor.SongTagEditorActivity
import com.maxfour.libreplayer.dialogs.*
import com.maxfour.libreplayer.extensions.hide
import com.maxfour.libreplayer.fragments.player.PlayerAlbumCoverFragment
import com.maxfour.libreplayer.helper.MusicPlayerRemote
import com.maxfour.libreplayer.interfaces.PaletteColorHolder
import com.maxfour.libreplayer.model.Song
import com.maxfour.libreplayer.model.lyrics.Lyrics
import com.maxfour.libreplayer.util.*
import kotlinx.android.synthetic.main.shadow_statusbar_toolbar.*
import java.io.FileNotFoundException

abstract class AbsPlayerFragment : AbsMusicServiceFragment(),
        Toolbar.OnMenuItemClickListener,
        PaletteColorHolder,
        PlayerAlbumCoverFragment.Callbacks {

    var callbacks: Callbacks? = null
        private set
    private var updateIsFavoriteTask: AsyncTask<*, *, *>? = null
    private var updateLyricsAsyncTask: AsyncTask<*, *, *>? = null
    private var playerAlbumCoverFragment: PlayerAlbumCoverFragment? = null

    override fun onAttach(
            context: Context
    ) {
        super.onAttach(context)
        try {
            callbacks = context as Callbacks?
        } catch (e: ClassCastException) {
            throw RuntimeException(context.javaClass.simpleName + " must implement " + Callbacks::class.java.simpleName)
        }
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onMenuItemClick(
            item: MenuItem
    ): Boolean {
        val song = MusicPlayerRemote.currentSong
        when (item.itemId) {
            R.id.action_toggle_favorite -> {
                toggleFavorite(song)
                return true
            }
            R.id.action_share -> {
                SongShareDialog.create(song).show(requireFragmentManager(), "SHARE_SONG")
                return true
            }
            R.id.action_delete_from_device -> {
                DeleteSongsDialog.create(song).show(requireFragmentManager(), "DELETE_SONGS")
                return true
            }
            R.id.action_add_to_playlist -> {
                AddToPlaylistDialog.create(song).show(requireFragmentManager(), "ADD_PLAYLIST")
                return true
            }
            R.id.action_clear_playing_queue -> {
                MusicPlayerRemote.clearQueue()
                return true
            }
            R.id.action_save_playing_queue -> {
                CreatePlaylistDialog.create(MusicPlayerRemote.playingQueue)
                        .show(requireFragmentManager(), "ADD_TO_PLAYLIST")
                return true
            }
            R.id.action_tag_editor -> {
                val intent = Intent(activity, SongTagEditorActivity::class.java)
                intent.putExtra(AbsTagEditorActivity.EXTRA_ID, song.id)
                startActivity(intent)
                return true
            }
            R.id.action_details -> {
                SongDetailDialog.create(song).show(requireFragmentManager(), "SONG_DETAIL")
                return true
            }
            R.id.action_go_to_album -> {
                NavigationUtil.goToAlbum(requireActivity(), song.albumId)
                return true
            }
            R.id.action_go_to_artist -> {
                NavigationUtil.goToArtist(requireActivity(), song.artistId)
                return true
            }
            R.id.now_playing -> {
                NavigationUtil.goToPlayingQueue(requireActivity())
                return true
            }
            R.id.action_show_lyrics -> {
                NavigationUtil.goToLyrics(requireActivity())
                return true
            }
            R.id.action_equalizer -> {
                NavigationUtil.openEqualizer(requireActivity())
                return true
            }
            R.id.action_sleep_timer -> {
                SleepTimerDialog().show(requireFragmentManager(), TAG)
                return true
            }
            R.id.action_set_as_ringtone -> {
                if (RingtoneManager.requiresDialog(requireActivity())) {
                    RingtoneManager.getDialog(requireActivity())
                }
                val ringtoneManager = RingtoneManager(requireActivity())
                ringtoneManager.setRingtone(song)
                return true
            }
            R.id.action_go_to_genre -> {
                val retriever = MediaMetadataRetriever()
                val songUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, song.id.toLong())
                retriever.setDataSource(activity, songUri)
                var genre: String? = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE)
                if (genre == null) {
                    genre = "Not Specified"
                }
                Toast.makeText(context, genre, Toast.LENGTH_SHORT).show()
                return true
            }
        }
        return false
    }

    protected open fun toggleFavorite(song: Song) {
        MusicUtil.toggleFavorite(requireActivity(), song)
    }

    abstract fun playerToolbar(): Toolbar

    abstract fun onShow()

    abstract fun onHide()

    abstract fun onBackPressed(): Boolean

    abstract fun toolbarIconColor(): Int

    override fun onServiceConnected() {
        updateIsFavorite()
        updateLyrics()
    }

    override fun onPlayingMetaChanged() {
        updateIsFavorite()
        updateLyrics()
    }

    override fun onDestroyView() {
        if (updateIsFavoriteTask != null && !updateIsFavoriteTask!!.isCancelled) {
            updateIsFavoriteTask!!.cancel(true)
        }
        if (updateLyricsAsyncTask != null && !updateLyricsAsyncTask!!.isCancelled) {
            updateLyricsAsyncTask!!.cancel(true)
        }
        super.onDestroyView()
    }

    @SuppressLint("StaticFieldLeak")
    fun updateIsFavorite() {
        if (updateIsFavoriteTask != null) {
            updateIsFavoriteTask!!.cancel(false)
        }
        updateIsFavoriteTask = object : AsyncTask<Song, Void, Boolean>() {
            override fun doInBackground(vararg params: Song): Boolean {
                return MusicUtil.isFavorite(requireActivity(), params[0])
            }

            override fun onPostExecute(isFavorite: Boolean) {
                val res = if (isFavorite)
                    R.drawable.ic_favorite_white_24dp
                else
                    R.drawable.ic_favorite_border_white_24dp

                val drawable = PlayerUtil.getTintedVectorDrawable(requireContext(), res, toolbarIconColor())
                if (playerToolbar().menu.findItem(R.id.action_toggle_favorite) != null)
                    playerToolbar().menu.findItem(R.id.action_toggle_favorite).setIcon(drawable).title = if (isFavorite) getString(R.string.action_remove_from_favorites) else getString(R.string.action_add_to_favorites)

            }
        }.execute(MusicPlayerRemote.currentSong)
    }

    @SuppressLint("StaticFieldLeak")
    private fun updateLyrics() {
        if (updateLyricsAsyncTask != null) updateLyricsAsyncTask!!.cancel(false)

        updateLyricsAsyncTask = object : AsyncTask<Song, Void, Lyrics>() {
            override fun onPreExecute() {
                super.onPreExecute()
                setLyrics(null)
            }

            override fun doInBackground(vararg params: Song): Lyrics? {
                try {
                    var data: String? = LyricUtil.getStringFromFile(params[0].title, params[0].artistName)
                    return if (TextUtils.isEmpty(data)) {
                        data = MusicUtil.getLyrics(params[0])
                        return if (TextUtils.isEmpty(data)) {
                            null
                        } else {
                            Lyrics.parse(params[0], data)
                        }
                    } else Lyrics.parse(params[0], data!!)
                } catch (err: FileNotFoundException) {
                    return null
                }
            }

            override fun onPostExecute(l: Lyrics?) {
                setLyrics(l)
            }

            override fun onCancelled(s: Lyrics?) {
                onPostExecute(null)
            }
        }.execute(MusicPlayerRemote.currentSong)
    }

    open fun setLyrics(l: Lyrics?) {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setBackgroundColor(ATHUtil.resolveColor(requireContext(), R.attr.colorSecondary))
        if (PreferenceUtil.getInstance(requireContext()).fullScreenMode &&
                view.findViewById<View>(R.id.status_bar) != null) {
            view.findViewById<View>(R.id.status_bar).visibility = View.GONE
        }
        playerAlbumCoverFragment = childFragmentManager.findFragmentById(R.id.playerAlbumCoverFragment) as PlayerAlbumCoverFragment?
        playerAlbumCoverFragment?.setCallbacks(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            statusBarShadow?.hide()
    }

    interface Callbacks {

        fun onPaletteColorChanged()
    }

    companion object {
        val TAG: String = AbsPlayerFragment::class.java.simpleName
        const val VISIBILITY_ANIM_DURATION: Long = 300
    }

    protected fun getUpNextAndQueueTime(): String {
        val duration = MusicPlayerRemote.getQueueDurationMillis(MusicPlayerRemote.position)

        return MusicUtil.buildInfoString(
                resources.getString(R.string.up_next),
                MusicUtil.getReadableDurationString(duration)
        )
    }
}
