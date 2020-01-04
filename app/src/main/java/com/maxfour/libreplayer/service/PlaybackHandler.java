package com.maxfour.libreplayer.service;

import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.maxfour.libreplayer.util.PreferenceUtil;

import java.lang.ref.WeakReference;

import static com.maxfour.libreplayer.service.MusicService.DUCK;
import static com.maxfour.libreplayer.service.MusicService.META_CHANGED;
import static com.maxfour.libreplayer.service.MusicService.PLAY_STATE_CHANGED;
import static com.maxfour.libreplayer.service.MusicService.REPEAT_MODE_NONE;
import static com.maxfour.libreplayer.service.MusicService.SONG_ENDED;
import static com.maxfour.libreplayer.service.MusicService.SONG_WENT_TO_NEXT;

class PlaybackHandler extends Handler {
    @NonNull
    private final WeakReference<MusicService> mService;
    private float currentDuckVolume = 1.0f;

    PlaybackHandler(final MusicService service, @NonNull final Looper looper) {
        super(looper);
        mService = new WeakReference<>(service);
    }

    @Override
    public void handleMessage(@NonNull final Message msg) {
        final MusicService service = mService.get();
        if (service == null) {
            return;
        }

        switch (msg.what) {
            case MusicService.DUCK:
                if (PreferenceUtil.getInstance(service).audioDucking()) {
                    currentDuckVolume -= .05f;
                    if (currentDuckVolume > .2f) {
                        sendEmptyMessageDelayed(DUCK, 10);
                    } else {
                        currentDuckVolume = .2f;
                    }
                } else {
                    currentDuckVolume = 1f;
                }
                service.playback.setVolume(currentDuckVolume);
                break;

            case MusicService.UNDUCK:
                if (PreferenceUtil.getInstance(service).audioDucking()) {
                    currentDuckVolume += .03f;
                    if (currentDuckVolume < 1f) {
                        sendEmptyMessageDelayed(MusicService.UNDUCK, 10);
                    } else {
                        currentDuckVolume = 1f;
                    }
                } else {
                    currentDuckVolume = 1f;
                }
                service.playback.setVolume(currentDuckVolume);
                break;

            case SONG_WENT_TO_NEXT:
                if (service.getRepeatMode() == REPEAT_MODE_NONE && service.isLastSong()) {
                    service.pause();
                    service.seek(0);
                } else {
                    service.position = service.nextPosition;
                    service.prepareNextImpl();
                    service.notifyChange(META_CHANGED);
                }
                break;

            case SONG_ENDED:
                // if there is a timer finished, don't continue
                if (service.pendingQuit ||
                        service.getRepeatMode() == REPEAT_MODE_NONE && service.isLastSong()) {
                    service.notifyChange(PLAY_STATE_CHANGED);
                    service.seek(0);
                    if (service.pendingQuit) {
                        service.pendingQuit = false;
                        service.quit();
                        break;
                    }
                } else {
                    service.playNextSong(false);
                }
                sendEmptyMessage(MusicService.RELEASE_WAKELOCK);
                break;

            case MusicService.RELEASE_WAKELOCK:
                service.releaseWakeLock();
                break;

            case MusicService.PLAY_SONG:
                service.playSongAtImpl(msg.arg1);
                break;

            case MusicService.SET_POSITION:
                service.openSongAndPrepareNextAt(msg.arg1);
                service.notifyChange(PLAY_STATE_CHANGED);
                break;

            case MusicService.PREPARE_NEXT:
                service.prepareNextImpl();
                break;

            case MusicService.RESTORE_QUEUES:
                service.restoreQueuesAndPositionIfNecessary();
                break;

            case MusicService.FOCUS_CHANGE:
                switch (msg.arg1) {
                    case AudioManager.AUDIOFOCUS_GAIN:
                        if (!service.isPlaying() && service.isPausedByTransientLossOfFocus()) {
                            service.play();
                            service.setPausedByTransientLossOfFocus(false);
                        }
                        removeMessages(DUCK);
                        sendEmptyMessage(MusicService.UNDUCK);
                        break;

                    case AudioManager.AUDIOFOCUS_LOSS:
                        // Lost focus for an unbounded amount of time: stop playback and release media playback
                        service.pause();
                        break;

                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                        // Lost focus for a short time, but we have to stop
                        // playback. We don't release the media playback because playback
                        // is likely to resume
                        boolean wasPlaying = service.isPlaying();
                        service.pause();
                        service.setPausedByTransientLossOfFocus(wasPlaying);
                        break;

                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                        // Lost focus for a short time, but it's ok to keep playing
                        // at an attenuated level
                        removeMessages(MusicService.UNDUCK);
                        sendEmptyMessage(DUCK);
                        break;
                }
                break;
        }
    }
}
