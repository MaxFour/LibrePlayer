package com.maxfour.music.dagger.module

import android.app.Activity

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ActivityModule(private val activity: Activity) {

    @Provides
    @Singleton
    fun provideActivity(): Activity {
        return activity
    }
}
