package com.maxfour.libreplayer.glide.artistimage

import android.content.Context
import com.bumptech.glide.Priority
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.GenericLoaderFactory
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.stream.StreamModelLoader
import com.maxfour.libreplayer.deezer.Data
import com.maxfour.libreplayer.deezer.DeezerApiService
import com.maxfour.libreplayer.util.MusicUtil
import com.maxfour.libreplayer.util.PreferenceUtil
import okhttp3.OkHttpClient
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.TimeUnit

class ArtistImage(val artistName: String, val skipOkHttpCache: Boolean)

class ArtistImageFetcher(
        private val context: Context,
        private val deezerApiService: DeezerApiService,
        val model: ArtistImage,
        val urlLoader: ModelLoader<GlideUrl, InputStream>,
        val width: Int,
        val height: Int
) : DataFetcher<InputStream> {

    private var urlFetcher: DataFetcher<InputStream>? = null
    private var isCancelled: Boolean = false

    override fun cleanup() {
        urlFetcher?.cleanup()
    }

    override fun getId(): String {
        return model.artistName
    }

    override fun cancel() {
        isCancelled = true
        urlFetcher?.cancel()
    }

    override fun loadData(priority: Priority?): InputStream? {
        if (!MusicUtil.isArtistNameUnknown(model.artistName) && PreferenceUtil.isAllowedToDownloadMetadata(context)) {
            val artists = model.artistName.split(",")
            val response = deezerApiService.getArtistImage(artists[0]).execute()

            if (!response.isSuccessful) {
                throw   IOException("Request failed with code: " + response.code());
            }

            if (isCancelled) return null

            return try {
                val deezerResponse = response.body();
                val imageUrl = deezerResponse?.data?.get(0)?.let { getHighestQuality(it) }
                val glideUrl = GlideUrl(imageUrl)
                urlFetcher = urlLoader.getResourceFetcher(glideUrl, width, height)
                urlFetcher?.loadData(priority)
            } catch (e: Exception) {
                null
            }
        } else return null
    }

    private fun getHighestQuality(imageUrl: Data): String {
        return when {
            imageUrl.pictureXl.isNotEmpty() -> imageUrl.pictureXl
            imageUrl.pictureBig.isNotEmpty() -> imageUrl.pictureBig
            imageUrl.pictureMedium.isNotEmpty() -> imageUrl.pictureMedium
            imageUrl.pictureSmall.isNotEmpty() -> imageUrl.pictureSmall
            imageUrl.picture.isNotEmpty() -> imageUrl.picture
            else -> ""
        }
    }
}

class ArtistImageLoader(
        val context: Context,
        private val deezerApiService: DeezerApiService,
        private val urlLoader: ModelLoader<GlideUrl, InputStream>
) : StreamModelLoader<ArtistImage> {

    override fun getResourceFetcher(model: ArtistImage, width: Int, height: Int): DataFetcher<InputStream> {
        return ArtistImageFetcher(context, deezerApiService, model, urlLoader, width, height)
    }
}

class Factory(
        val context: Context
) : ModelLoaderFactory<ArtistImage, InputStream> {
    private var deezerApiService: DeezerApiService
    private var okHttpFactory: OkHttpUrlLoader.Factory

    init {
        okHttpFactory = OkHttpUrlLoader.Factory(OkHttpClient.Builder()
                .connectTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .build())
        deezerApiService = DeezerApiService.invoke(DeezerApiService.createDefaultOkHttpClient(context)
                .connectTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .build())
    }

    override fun build(context: Context?, factories: GenericLoaderFactory?): ModelLoader<ArtistImage, InputStream> {
        return ArtistImageLoader(context!!, deezerApiService, okHttpFactory.build(context, factories))
    }

    override fun teardown() {
        okHttpFactory.teardown()
    }

    companion object {
        // we need these very low values to make sure our artist image loading calls doesn't block the image loading queue
        private const val TIMEOUT: Long = 700
    }

}
