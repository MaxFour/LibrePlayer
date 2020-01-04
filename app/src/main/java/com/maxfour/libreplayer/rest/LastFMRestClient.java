package com.maxfour.libreplayer.rest;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.maxfour.libreplayer.rest.service.LastFMService;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LastFMRestClient {

    private static final String BASE_URL = "https://ws.audioscrobbler.com/2.0/";

    private LastFMService apiService;

    public LastFMRestClient(@NonNull Context context) {
        this(createDefaultOkHttpClientBuilder(context).build());
    }

    private LastFMRestClient(@NonNull Call.Factory client) {
        Retrofit restAdapter = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .callFactory(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = restAdapter.create(LastFMService.class);
    }

    @Nullable
    private static Cache createDefaultCache(Context context) {
        File cacheDir = new File(context.getCacheDir().getAbsolutePath(), "/okhttp-lastfm/");
        if (cacheDir.mkdirs() || cacheDir.isDirectory()) {
            return new Cache(cacheDir, 1024 * 1024 * 10);
        }
        return null;
    }

    private static Interceptor createCacheControlInterceptor() {
        return chain -> {
            Request modifiedRequest = chain.request().newBuilder()
                    .addHeader("Cache-Control", "max-age=31536000, max-stale=31536000")
                    .build();
            return chain.proceed(modifiedRequest);
        };
    }

    @NonNull
    private static OkHttpClient.Builder createDefaultOkHttpClientBuilder(@NonNull Context context) {
        return new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(0, 1, TimeUnit.NANOSECONDS))
                .retryOnConnectionFailure(true)
                .connectTimeout(1, TimeUnit.MINUTES) // connect timeout
                .writeTimeout(1, TimeUnit.MINUTES) // write timeout
                .readTimeout(1, TimeUnit.MINUTES) // read timeout
                .cache(createDefaultCache(context))
                .addInterceptor(createCacheControlInterceptor());
    }

    @NonNull
    public LastFMService getApiService() {
        return apiService;
    }
}
