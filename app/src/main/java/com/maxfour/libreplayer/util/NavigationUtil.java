package com.maxfour.libreplayer.util;

import static com.maxfour.libreplayer.util.PlayerUtil.openUrl;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.audiofx.AudioEffect;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.maxfour.libreplayer.R;
import com.maxfour.libreplayer.activities.AboutActivity;
import com.maxfour.libreplayer.activities.AlbumDetailsActivity;
import com.maxfour.libreplayer.activities.ArtistDetailActivity;
import com.maxfour.libreplayer.activities.GenreDetailsActivity;
import com.maxfour.libreplayer.activities.LyricsActivity;
import com.maxfour.libreplayer.activities.PlayingQueueActivity;
import com.maxfour.libreplayer.activities.PlaylistDetailActivity;
import com.maxfour.libreplayer.activities.SearchActivity;
import com.maxfour.libreplayer.activities.SettingsActivity;
import com.maxfour.libreplayer.activities.UserInfoActivity;
import com.maxfour.libreplayer.helper.MusicPlayerRemote;
import com.maxfour.libreplayer.model.Genre;
import com.maxfour.libreplayer.model.Playlist;

import org.jetbrains.annotations.NotNull;

public class NavigationUtil {

    public static void goToAbout(@NonNull Activity activity) {
        ActivityCompat.startActivity(activity, new Intent(activity, AboutActivity.class), null);
    }

    public static void goToAlbum(@NonNull Activity activity, int albumId) {
        Intent intent = new Intent(activity, AlbumDetailsActivity.class);
        intent.putExtra(AlbumDetailsActivity.EXTRA_ALBUM_ID, albumId);
        ActivityCompat.startActivity(activity, intent, null);
    }

    public static void goToAlbumOptions(@NonNull Activity activity,
                                        int albumId,
                                        @NonNull ActivityOptions options) {
        Intent intent = new Intent(activity, AlbumDetailsActivity.class);
        intent.putExtra(AlbumDetailsActivity.EXTRA_ALBUM_ID, albumId);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    public static void goToArtist(@NonNull Activity activity, int i) {
        Intent intent = new Intent(activity, ArtistDetailActivity.class);
        intent.putExtra(ArtistDetailActivity.EXTRA_ARTIST_ID, i);
        ActivityCompat.startActivity(activity, intent, null);
    }

    public static void goToArtistOptions(@NotNull AppCompatActivity activity,
            int artistId,
            @NonNull ActivityOptions options) {

        Intent intent = new Intent(activity, ArtistDetailActivity.class);
        intent.putExtra(ArtistDetailActivity.EXTRA_ARTIST_ID, artistId);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    public static void goToGenre(@NonNull Activity activity, @NonNull Genre genre) {
        Intent intent = new Intent(activity, GenreDetailsActivity.class);
        intent.putExtra(GenreDetailsActivity.EXTRA_GENRE_ID, genre);
        ActivityCompat.startActivity(activity, intent, null);
    }

    public static void goToLyrics(@NonNull Activity activity) {
        Intent intent = new Intent(activity, LyricsActivity.class);
        ActivityCompat.startActivity(activity, intent, null);
    }

    public static void goToPlayingQueue(@NonNull Activity activity) {
        Intent intent = new Intent(activity, PlayingQueueActivity.class);
        ActivityCompat.startActivity(activity, intent, null);
    }

    public static void goToPlaylistNew(@NonNull Activity activity, @NonNull Playlist playlist) {
        Intent intent = new Intent(activity, PlaylistDetailActivity.class);
        intent.putExtra(PlaylistDetailActivity.Companion.getEXTRA_PLAYLIST(), playlist);
        ActivityCompat.startActivity(activity, intent, null);
    }

    public static void goToSearch(@NonNull Activity activity,
            @NonNull ActivityOptions activityOptions) {
        ActivityCompat.startActivity(activity, new Intent(activity, SearchActivity.class),
                activityOptions.toBundle());
    }

    public static void goToSearch(@NonNull Activity activity, boolean isMicOpen,
            @NonNull ActivityOptions activityOptions) {
        ActivityCompat.startActivity(activity, new Intent(activity, SearchActivity.class)
                        .putExtra(SearchActivity.EXTRA_SHOW_MIC, isMicOpen),
                activityOptions.toBundle());
    }

    public static void goToSettings(@NonNull Activity activity) {
        ActivityCompat.startActivity(activity, new Intent(activity, SettingsActivity.class), null);
    }

    public static void goToUserInfo(@NonNull Activity activity,
                                    @NonNull ActivityOptions activityOptions) {
        ActivityCompat.startActivity(activity, new Intent(activity, UserInfoActivity.class),
                activityOptions.toBundle());
    }

    public static void openEqualizer(@NonNull final Activity activity) {
        stockEqalizer(activity);
    }

    private static void stockEqalizer(@NonNull Activity activity) {
        final int sessionId = MusicPlayerRemote.INSTANCE.getAudioSessionId();
        if (sessionId == AudioEffect.ERROR_BAD_VALUE) {
            Toast.makeText(activity, activity.getResources().getString(R.string.no_audio_ID),
                    Toast.LENGTH_LONG).show();
        } else {
            try {
                final Intent effects = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
                effects.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, sessionId);
                effects.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC);
                activity.startActivityForResult(effects, 0);
            } catch (@NonNull final ActivityNotFoundException notFound) {
                Toast.makeText(activity, activity.getResources().getString(R.string.no_equalizer),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

}
