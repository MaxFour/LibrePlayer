package com.maxfour.music.model;

import android.content.Context;
import android.os.Parcel;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import io.reactivex.Observable;

public abstract class AbsCustomPlaylist extends Playlist {
    public AbsCustomPlaylist(int id, String name) {
        super(id, name);
    }

    public AbsCustomPlaylist() {
    }

    public AbsCustomPlaylist(Parcel in) {
        super(in);
    }

    @NonNull
    public abstract Observable<ArrayList<Song>> getSongsFlowable(@NotNull Context context);

    @NonNull
    public abstract ArrayList<Song> getSongs(@NotNull Context context);
}
