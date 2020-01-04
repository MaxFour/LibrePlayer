package com.maxfour.libreplayer.helper;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.maxfour.libreplayer.model.Playlist;
import com.maxfour.libreplayer.model.Song;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class M3UWriter implements M3UConstants {

    @Nullable
    public static File write(@NonNull Context context,
                             @NonNull File dir,
                             @NonNull Playlist playlist) throws IOException {
        if (!dir.exists()) //noinspection ResultOfMethodCallIgnored
            dir.mkdirs();
        File file = new File(dir, playlist.name.concat("." + EXTENSION));

        ArrayList<Song> songs = playlist.getSongs(context);

        if (songs.size() > 0) {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));

            bw.write(HEADER);
            for (Song song : songs) {
                bw.newLine();
                bw.write(ENTRY + song.getDuration() + DURATION_SEPARATOR + song.getArtistName() + " - " + song.getTitle());
                bw.newLine();
                bw.write(song.getData());
            }

            bw.close();
        }
        return file;
    }
}
