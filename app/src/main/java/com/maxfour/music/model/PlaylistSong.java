package com.maxfour.music.model;

import org.jetbrains.annotations.NotNull;

import kotlinx.android.parcel.Parcelize;

@Parcelize
public class PlaylistSong extends Song {
    final int playlistId;
    final int idInPlayList;

    public PlaylistSong(int id,
                        @NotNull String title,
                        int trackNumber,
                        int year,
                        long duration,
                        @NotNull String data,
                        long dateModified,
                        int albumId,
                        @NotNull String albumName,
                        int artistId,
                        @NotNull String artistName,
                        int playlistId,
                        int idInPlayList,
                        @NotNull String composer) {
        super(id, title, trackNumber, year, duration, data, dateModified, albumId, albumName, artistId, artistName, composer);
        this.playlistId = playlistId;
        this.idInPlayList = idInPlayList;
    }

    public int getPlaylistId() {
        return playlistId;
    }

    public int getIdInPlayList() {
        return idInPlayList;
    }
}
