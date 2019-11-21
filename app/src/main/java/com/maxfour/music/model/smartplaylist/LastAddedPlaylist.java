package com.maxfour.music.model.smartplaylist;

import android.content.Context;
import android.os.Parcel;

import androidx.annotation.NonNull;

import com.maxfour.music.R;
import com.maxfour.music.loaders.LastAddedSongsLoader;
import com.maxfour.music.model.Song;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import io.reactivex.Observable;

public class LastAddedPlaylist extends AbsSmartPlaylist {

    public static final Creator<LastAddedPlaylist> CREATOR = new Creator<LastAddedPlaylist>() {
        public LastAddedPlaylist createFromParcel(Parcel source) {
            return new LastAddedPlaylist(source);
        }

        public LastAddedPlaylist[] newArray(int size) {
            return new LastAddedPlaylist[size];
        }
    };

    public LastAddedPlaylist(@NonNull Context context) {
        super(context.getString(R.string.last_added), R.drawable.ic_library_add_white_24dp);
    }

    protected LastAddedPlaylist(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public Observable<ArrayList<Song>> getSongsFlowable(@NotNull @NonNull Context context) {
        return LastAddedSongsLoader.INSTANCE.getLastAddedSongsFlowable(context);
    }

    @NonNull
    @Override
    public ArrayList<Song> getSongs(@NotNull @NonNull Context context) {
        return LastAddedSongsLoader.INSTANCE.getLastAddedSongs(context);
    }

    @Override
    public void clear(@NonNull Context context) {
    }

    @Override
    public boolean isClearable() {
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
