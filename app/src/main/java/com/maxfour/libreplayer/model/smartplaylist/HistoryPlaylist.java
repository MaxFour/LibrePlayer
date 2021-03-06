package com.maxfour.libreplayer.model.smartplaylist;

import android.content.Context;
import android.os.Parcel;

import androidx.annotation.NonNull;

import com.maxfour.libreplayer.R;
import com.maxfour.libreplayer.loaders.TopAndRecentlyPlayedSongsLoader;
import com.maxfour.libreplayer.model.Song;
import com.maxfour.libreplayer.providers.HistoryStore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class HistoryPlaylist extends AbsSmartPlaylist {

    public static final Creator<HistoryPlaylist> CREATOR = new Creator<HistoryPlaylist>() {
        public HistoryPlaylist createFromParcel(Parcel source) {
            return new HistoryPlaylist(source);
        }

        public HistoryPlaylist[] newArray(int size) {
            return new HistoryPlaylist[size];
        }
    };

    public HistoryPlaylist(@NonNull Context context) {
        super(context.getString(R.string.history), R.drawable.ic_access_time_white_24dp);
    }

    protected HistoryPlaylist(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public ArrayList<Song> getSongs(@NotNull @NonNull Context context) {
        return TopAndRecentlyPlayedSongsLoader.INSTANCE.getRecentlyPlayedSongs(context);
    }

    @Override
    public void clear(@NonNull Context context) {
        HistoryStore.getInstance(context).clear();
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
