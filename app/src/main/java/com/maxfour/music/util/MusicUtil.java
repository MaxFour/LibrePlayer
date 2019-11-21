package com.maxfour.music.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.maxfour.music.R;
import com.maxfour.music.helper.MusicPlayerRemote;
import com.maxfour.music.loaders.PlaylistLoader;
import com.maxfour.music.loaders.SongLoader;
import com.maxfour.music.model.Artist;
import com.maxfour.music.model.Playlist;
import com.maxfour.music.model.Song;
import com.maxfour.music.model.lyrics.AbsSynchronizedLyrics;
import com.maxfour.music.service.MusicService;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class MusicUtil {

    public static final String TAG = MusicUtil.class.getSimpleName();
    private static Playlist playlist;

    public static Uri getMediaStoreAlbumCoverUri(int albumId) {
        final Uri sArtworkUri = Uri
                .parse("content://media/external/audio/albumart");

        return ContentUris.withAppendedId(sArtworkUri, albumId);
    }

    public static Uri getSongFileUri(int songId) {
        return ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songId);
    }

    @NonNull
    public static Intent createShareSongFileIntent(@NonNull final Song song, @NonNull Context context) {
        try {
            return new Intent()
                    .setAction(Intent.ACTION_SEND)
                    .putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName(), new File(song.getData())))
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    .setType("audio/*");
        } catch (IllegalArgumentException e) {
            // TODO the path is most likely not like /storage/emulated/0/... but something like /storage/28C7-75B0/...
            e.printStackTrace();
            Toast.makeText(context, "Could not share this file, I'm aware of the issue.", Toast.LENGTH_SHORT).show();
            return new Intent();
        }
    }

    @NonNull
    public static String getSongCountString(@NonNull final Context context, int songCount) {
        final String songString = songCount == 1 ? context.getResources().getString(R.string.song) : context.getResources().getQuantityString(R.plurals.songs_x, songCount);
        return songCount + " " + songString;
    }

    @NonNull
    public static String getSongInfoString(@NonNull Song song) {
        return MusicUtil.buildInfoString(
                song.getArtistName(),
                song.getAlbumName()
        );
    }

    @NonNull
    public static String getArtistInfoString(@NonNull final Context context,
                                             @NonNull final Artist artist) {
        int albumCount = artist.getAlbumCount();
        int songCount = artist.getSongCount();
        String albumString = albumCount == 1 ? context.getResources().getString(R.string.album)
                : context.getResources().getQuantityString(R.plurals.albums_x, albumCount);
        String songString = songCount == 1 ? context.getResources().getString(R.string.song)
                : context.getResources().getQuantityString(R.plurals.songs_x, songCount);
        return albumCount + " " + albumString + " • " + songCount + " " + songString;
    }

    @NonNull
    public static String getPlaylistInfoString(@NonNull final Context context, @NonNull List<Song> songs) {
        final long duration = getTotalDuration(context, songs);

        return MusicUtil.buildInfoString(
                MusicUtil.getSongCountString(context, songs.size()),
                MusicUtil.getReadableDurationString(duration)
        );
    }


    /**
     * Build a concatenated string from the provided arguments
     * The intended purpose is to show extra annotations
     * to a music library item.
     * Ex: for a given album --> buildInfoString(album.artist, album.songCount)
     */
    @NonNull
    public static String buildInfoString(@Nullable final String string1, @Nullable final String string2) {
        // Skip empty strings
        if (TextUtils.isEmpty(string1)) {
            //noinspection ConstantConditions
            return TextUtils.isEmpty(string2) ? "" : string2;
        }
        if (TextUtils.isEmpty(string2)) {
            //noinspection ConstantConditions
            return TextUtils.isEmpty(string1) ? "" : string1;
        }

        return string1 + "  •  " + string2;
    }

    public static String getReadableDurationString(long songDurationMillis) {
        long minutes = (songDurationMillis / 1000) / 60;
        long seconds = (songDurationMillis / 1000) % 60;
        if (minutes < 60) {
            return String.format(Locale.getDefault(), "%01d:%02d", minutes, seconds);
        } else {
            long hours = minutes / 60;
            minutes = minutes % 60;
            return String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds);
        }
    }

    //iTunes uses for example 1002 for track 2 CD1 or 3011 for track 11 CD3.
    //this method converts those values to normal tracknumbers
    public static int getFixedTrackNumber(int trackNumberToFix) {
        return trackNumberToFix % 1000;
    }

    public static void insertAlbumArt(@NonNull Context context, int albumId, String path) {
        ContentResolver contentResolver = context.getContentResolver();

        Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
        contentResolver.delete(ContentUris.withAppendedId(artworkUri, albumId), null, null);

        ContentValues values = new ContentValues();
        values.put("album_id", albumId);
        values.put("_data", path);

        contentResolver.insert(artworkUri, values);
    }

    @NonNull
    public static File createAlbumArtFile() {
        return new File(createAlbumArtDir(), String.valueOf(System.currentTimeMillis()));
    }

    @NonNull
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static File createAlbumArtDir() {
        File albumArtDir = new File(Environment.getExternalStorageDirectory(), "/albumthumbs/");
        if (!albumArtDir.exists()) {
            albumArtDir.mkdirs();
            try {
                new File(albumArtDir, ".nomedia").createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return albumArtDir;
    }

    public static void deleteTracks(@NonNull final Activity activity,
                                    @NonNull final List<Song> songs,
                                    @Nullable final List<Uri> safUris,
                                    @Nullable final Runnable callback) {
        final String[] projection = new String[]{
                BaseColumns._ID, MediaStore.MediaColumns.DATA
        };

        // Split the query into multiple batches, and merge the resulting cursors
        int batchStart = 0;
        int batchEnd = 0;
        final int batchSize = 1000000 / 10; // 10^6 being the SQLite limite on the query lenth in bytes, 10 being the max number of digits in an int, used to store the track ID
        final int songCount = songs.size();

        while (batchEnd < songCount) {
            batchStart = batchEnd;

            final StringBuilder selection = new StringBuilder();
            selection.append(BaseColumns._ID + " IN (");

            for (int i = 0; (i < batchSize - 1) && (batchEnd < songCount - 1); i++, batchEnd++) {
                selection.append(songs.get(batchEnd).getId());
                selection.append(",");
            }
            // The last element of a batch
            selection.append(songs.get(batchEnd).getId());
            batchEnd++;
            selection.append(")");

            try {
                final Cursor cursor = activity.getContentResolver().query(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection.toString(),
                        null, null);
                // TODO: At this point, there is no guarantee that the size of the cursor is the same as the size of the selection string.
                // Despite that, the Step 3 assumes that the safUris elements are tracking closely the content of the cursor.

                if (cursor != null) {
                    // Step 1: Remove selected tracks from the current playlist, as well
                    // as from the album art cache
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        final int id = cursor.getInt(0);
                        final Song song = SongLoader.INSTANCE.getSong(activity, id);
                        MusicPlayerRemote.INSTANCE.removeFromQueue(song);
                        cursor.moveToNext();
                    }

                    // Step 2: Remove selected tracks from the database
                    activity.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            selection.toString(), null);

                    // Step 3: Remove files from card
                    cursor.moveToFirst();
                    int i = batchStart;
                    while (!cursor.isAfterLast()) {
                        final String name = cursor.getString(1);
                        final Uri safUri = safUris == null || safUris.size() <= i ? null : safUris.get(i);
                        SAFUtil.delete(activity, name, safUri);
                        i++;
                        cursor.moveToNext();
                    }
                    cursor.close();
                }
            } catch (SecurityException ignored) {
            }
        }

        activity.getContentResolver().notifyChange(Uri.parse("content://media"), null);

        activity.runOnUiThread(() -> {
            Toast.makeText(activity, activity.getResources().getQuantityString(R.plurals.deleted_x_songs, songs.size(),songs.size()), Toast.LENGTH_SHORT).show();
            if (callback != null) {
                callback.run();
            }
        });
    }

    public static void deleteAlbumArt(@NonNull Context context, int albumId) {
        ContentResolver contentResolver = context.getContentResolver();
        Uri localUri = Uri.parse("content://media/external/audio/albumart");
        contentResolver.delete(ContentUris.withAppendedId(localUri, albumId), null, null);
    }


    @Nullable
    public static String getLyrics(@NonNull Song song) {
        String lyrics = null;

        File file = new File(song.getData());

        try {
            lyrics = AudioFileIO.read(file).getTagOrCreateDefault().getFirst(FieldKey.LYRICS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (lyrics == null || lyrics.trim().isEmpty() || !AbsSynchronizedLyrics
                .isSynchronized(lyrics)) {
            File dir = file.getAbsoluteFile().getParentFile();

            if (dir != null && dir.exists() && dir.isDirectory()) {
                String format = ".*%s.*\\.(lrc|txt)";
                String filename = Pattern.quote(FileUtil.stripExtension(file.getName()));
                String songtitle = Pattern.quote(song.getTitle());

                final ArrayList<Pattern> patterns = new ArrayList<>();
                patterns.add(Pattern.compile(String.format(format, filename),
                        Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE));
                patterns.add(Pattern.compile(String.format(format, songtitle),
                        Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE));

                File[] files = dir.listFiles(f -> {
                    for (Pattern pattern : patterns) {
                        if (pattern.matcher(f.getName()).matches()) {
                            return true;
                        }
                    }
                    return false;
                });

                if (files != null && files.length > 0) {
                    for (File f : files) {
                        try {
                            String newLyrics = FileUtil.read(f);
                            if (newLyrics != null && !newLyrics.trim().isEmpty()) {
                                if (AbsSynchronizedLyrics.isSynchronized(newLyrics)) {
                                    return newLyrics;
                                }
                                lyrics = newLyrics;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        return lyrics;
    }

    public static void toggleFavorite(@NonNull final Context context, @NonNull final Song song) {
        if (isFavorite(context, song)) {
            PlaylistsUtil.removeFromPlaylist(context, song, getFavoritesPlaylist(context).id);
        } else {
            PlaylistsUtil.addToPlaylist(context, song, getOrCreateFavoritesPlaylist(context).id,
                    false);
        }
        context.sendBroadcast(new Intent(MusicService.FAVORITE_STATE_CHANGED));
    }

    public static boolean isFavoritePlaylist(@NonNull final Context context,
                                             @NonNull final Playlist playlist) {
        return playlist.name != null && playlist.name.equals(context.getString(R.string.favorites));
    }

    private static Playlist getFavoritesPlaylist(@NonNull final Context context) {
        return PlaylistLoader.INSTANCE.getPlaylist(context, context.getString(R.string.favorites));
    }

    private static Playlist getOrCreateFavoritesPlaylist(@NonNull final Context context) {
        return PlaylistLoader.INSTANCE.getPlaylist(context,
                PlaylistsUtil.createPlaylist(context, context.getString(R.string.favorites)));
    }

    public static boolean isFavorite(@NonNull final Context context, @NonNull final Song song) {
        return PlaylistsUtil
                .doPlaylistContains(context, getFavoritesPlaylist(context).id, song.getId());
    }

    public static boolean isArtistNameUnknown(@Nullable String artistName) {
        if (TextUtils.isEmpty(artistName)) {
            return false;
        }
        if (artistName.equals(Artist.UNKNOWN_ARTIST_DISPLAY_NAME)) {
            return true;
        }
        artistName = artistName.trim().toLowerCase();
        return artistName.equals("unknown") || artistName.equals("<unknown>");
    }

    @NonNull
    public static String getSectionName(@Nullable String musicMediaTitle) {
        if (TextUtils.isEmpty(musicMediaTitle)) {
            return "";
        }
        musicMediaTitle = musicMediaTitle.trim().toLowerCase();
        if (musicMediaTitle.startsWith("the ")) {
            musicMediaTitle = musicMediaTitle.substring(4);
        } else if (musicMediaTitle.startsWith("a ")) {
            musicMediaTitle = musicMediaTitle.substring(2);
        }
        if (musicMediaTitle.isEmpty()) {
            return "";
        }
        return String.valueOf(musicMediaTitle.charAt(0)).toUpperCase();
    }

    @NonNull
    public static Playlist getPlaylist() {
        return playlist;
    }

    public static void setPlaylist(@NonNull Playlist playlist) {
        MusicUtil.playlist = playlist;
    }

    public static long getTotalDuration(@NonNull final Context context, @NonNull List<Song> songs) {
        long duration = 0;
        for (int i = 0; i < songs.size(); i++) {
            duration += songs.get(i).getDuration();
        }
        return duration;
    }

    public static int indexOfSongInList(List<Song> songs, int songId) {
        for (int i = 0; i < songs.size(); i++) {
            if (songs.get(i).getId() == songId) {
                return i;
            }
        }
        return -1;
    }

    @NonNull
    public static String getYearString(int year) {
        return year > 0 ? String.valueOf(year) : "-";
    }
}
