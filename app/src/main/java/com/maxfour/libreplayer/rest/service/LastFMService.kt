package com.maxfour.libreplayer.rest.service

import com.maxfour.libreplayer.rest.model.LastFmArtist
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface LastFMService {
    companion object {
        const val API_KEY = "c679c8d3efa84613dc7dcb2e8d42da4c"
        const val BASE_QUERY_PARAMETERS = "?format=json&autocorrect=1&api_key=$API_KEY"
    }

    @GET("$BASE_QUERY_PARAMETERS&method=artist.getinfo")
    suspend fun artistInfo(@Query("artist") artistName: String,
                           @Query("lang") language: String?,
                           @Header("Cache-Control") cacheControl: String?
    ): LastFmArtist
}
