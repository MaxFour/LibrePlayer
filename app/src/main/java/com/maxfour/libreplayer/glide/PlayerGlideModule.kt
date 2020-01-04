package com.maxfour.libreplayer.glide

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.module.GlideModule
import com.maxfour.libreplayer.glide.artistimage.ArtistImage
import com.maxfour.libreplayer.glide.artistimage.Factory
import com.maxfour.libreplayer.glide.audiocover.AudioFileCover
import com.maxfour.libreplayer.glide.audiocover.AudioFileCoverLoader
import java.io.InputStream

class PlayerGlideModule : GlideModule {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
    }

    override fun registerComponents(context: Context, glide: Glide) {
        glide.register(AudioFileCover::class.java, InputStream::class.java, AudioFileCoverLoader.Factory())
        glide.register(ArtistImage::class.java, InputStream::class.java, Factory(context))
    }
}
