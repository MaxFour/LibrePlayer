package com.maxfour.music.glide

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.module.GlideModule
import com.maxfour.music.glide.artistimage.ArtistImage
import com.maxfour.music.glide.artistimage.Factory
import com.maxfour.music.glide.audiocover.AudioFileCover
import com.maxfour.music.glide.audiocover.AudioFileCoverLoader
import java.io.InputStream

class MusicPlayerGlideModule : GlideModule {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
    }

    override fun registerComponents(context: Context, glide: Glide) {
        glide.register(AudioFileCover::class.java, InputStream::class.java, AudioFileCoverLoader.Factory())
        glide.register(ArtistImage::class.java, InputStream::class.java, Factory(context))
    }
}
