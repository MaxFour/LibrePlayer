package com.maxfour.libreplayer.model.smartplaylist;

import android.content.Context;
import android.os.Parcel;

import androidx.annotation.NonNull;

import com.maxfour.libreplayer.R;
import com.maxfour.libreplayer.loaders.TopAndRecentlyPlayedSongsLoader;
import com.maxfour.libreplayer.model.Song;
import com.maxfour.libreplayer.providers.SongPlayCountStore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MyTopSongsPlaylist extends AbsSmartPlaylist {

    public static final Creator<MyTopSongsPlaylist> CREATOR = new Creator<MyTopSongsPlaylist>() {
        public MyTopSongsPlaylist createFromParcel(Parcel source) {
            return new MyTopSongsPlaylist(source);
        }

        public MyTopSongsPlaylist[] newArray(int size) {
            return new MyTopSongsPlaylist[size];
        }
    };

    public MyTopSongsPlaylist(@NonNull Context context) {
        super(context.getString(R.string.my_top_songs), R.drawable.ic_trending_up_white_24dp);
    }

    protected MyTopSongsPlaylist(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public ArrayList<Song> getSongs(@NotNull @NonNull Context context) {
        return TopAndRecentlyPlayedSongsLoader.INSTANCE.getTopSongs(context);
    }

    @Override
    public void clear(@NonNull Context context) {
        SongPlayCountStore.getInstance(context).clear();
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
