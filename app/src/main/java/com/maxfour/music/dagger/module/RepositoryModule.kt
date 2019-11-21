package com.maxfour.music.dagger.module

import android.content.Context
import com.maxfour.music.providers.RepositoryImpl
import com.maxfour.music.providers.interfaces.Repository
import dagger.Module
import dagger.Provides

@Module(includes = [AppModule::class])
class RepositoryModule {

    @Provides
    fun providesRepository(context: Context): Repository {
        return RepositoryImpl(context)
    }
}
