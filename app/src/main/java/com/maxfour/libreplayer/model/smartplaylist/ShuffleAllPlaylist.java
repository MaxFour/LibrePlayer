package com.maxfour.libreplayer.model.smartplaylist;

import android.content.Context;
import android.os.Parcel;

import androidx.annotation.NonNull;

import com.maxfour.libreplayer.R;
import com.maxfour.libreplayer.loaders.SongLoader;
import com.maxfour.libreplayer.model.Song;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import io.reactivex.Observable;

public class ShuffleAllPlaylist extends AbsSmartPlaylist {

    public static final Creator<ShuffleAllPlaylist> CREATOR = new Creator<ShuffleAllPlaylist>() {
        public ShuffleAllPlaylist createFromParcel(Parcel source) {
            return new ShuffleAllPlaylist(source);
        }

        public ShuffleAllPlaylist[] newArray(int size) {
            return new ShuffleAllPlaylist[size];
        }
    };

    public ShuffleAllPlaylist(@NonNull Context context) {
        super(context.getString(R.string.action_shuffle_all), R.drawable.ic_shuffle_white_24dp);
    }

    protected ShuffleAllPlaylist(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public Observable<ArrayList<Song>> getSongsFlowable(@NotNull @NonNull Context context) {
        return SongLoader.INSTANCE.getAllSongsFlowable(context);
    }

    @NonNull
    @Override
    public ArrayList<Song> getSongs(@NotNull Context context) {
        return SongLoader.INSTANCE.getAllSongs(context);
    }

    @Override
    public void clear(@NonNull Context context) {
        // Shuffle all is not a real "Smart Playlist"
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
