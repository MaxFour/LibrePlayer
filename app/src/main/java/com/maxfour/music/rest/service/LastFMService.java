package com.maxfour.music.rest.service;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.maxfour.music.rest.model.LastFmAlbum;
import com.maxfour.music.rest.model.LastFmArtist;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface LastFMService {
    String API_KEY_BAK = "bd9c6ea4d55ec9ed3af7d276e5ece304";
    String API_KEY = "c679c8d3efa84613dc7dcb2e8d42da4c";
    String BASE_QUERY_PARAMETERS = "?format=json&autocorrect=1&api_key=" + API_KEY;
    String METHOD_TRACK = "track.getInfo";

    @NonNull
    @GET(BASE_QUERY_PARAMETERS + "&method=album.getinfo")
    Observable<LastFmAlbum> getAlbumInfo(@Query("album") @NonNull String albumName, @Query("artist") @NonNull String artistName, @Nullable @Query("lang") String language);

    @NonNull
    @GET(BASE_QUERY_PARAMETERS + "&method=artist.getinfo")
    Call<LastFmArtist> getArtistInfo(@Query("artist") @NonNull String artistName, @Nullable @Query("lang") String language, @Nullable @Header("Cache-Control") String cacheControl);

    @NonNull
    @GET(BASE_QUERY_PARAMETERS + "&method=artist.getinfo")
    Observable<LastFmArtist> getArtistInfoFloable(@Query("artist") @NonNull String artistName, @Nullable @Query("lang") String language, @Nullable @Header("Cache-Control") String cacheControl);

}