package com.maxfour.music.model.smartplaylist;

import android.content.Context;
import android.os.Parcel;

import androidx.annotation.NonNull;

import com.maxfour.music.R;
import com.maxfour.music.loaders.TopAndRecentlyPlayedTracksLoader;
import com.maxfour.music.model.Song;
import com.maxfour.music.providers.SongPlayCountStore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import io.reactivex.Observable;

public class MyTopTracksPlaylist extends AbsSmartPlaylist {

    public static final Creator<MyTopTracksPlaylist> CREATOR = new Creator<MyTopTracksPlaylist>() {
        public MyTopTracksPlaylist createFromParcel(Parcel source) {
            return new MyTopTracksPlaylist(source);
        }

        public MyTopTracksPlaylist[] newArray(int size) {
            return new MyTopTracksPlaylist[size];
        }
    };

    public MyTopTracksPlaylist(@NonNull Context context) {
        super(context.getString(R.string.my_top_songs), R.drawable.ic_trending_up_white_24dp);
    }

    protected MyTopTracksPlaylist(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public Observable<ArrayList<Song>> getSongsFlowable(@NotNull @NonNull Context context) {
        return TopAndRecentlyPlayedTracksLoader.INSTANCE.getTopTracksFlowable(context);
    }

    @NonNull
    @Override
    public ArrayList<Song> getSongs(@NotNull @NonNull Context context) {
        return TopAndRecentlyPlayedTracksLoader.INSTANCE.getTopTracks(context);
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
