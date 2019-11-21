package com.maxfour.music

import androidx.multidex.MultiDexApplication
import com.maxfour.appthemehelper.ThemeStore
import com.maxfour.appthemehelper.util.VersionUtils
import com.maxfour.music.appshortcuts.DynamicShortcutManager
import com.maxfour.music.dagger.DaggerMusicComponent
import com.maxfour.music.dagger.MusicComponent
import com.maxfour.music.dagger.module.AppModule

class App : MultiDexApplication() {

    override fun onCreate() {
       /* if (MissingSplitsManagerFactory.create(this).disableAppIfMissingRequiredSplits()) {
            return
        }*/
        super.onCreate()
        instance = this
        musicComponent = DaggerMusicComponent.builder()
                .appModule(AppModule(this))
                .build()

        // default theme
        if (!ThemeStore.isConfigured(this, 3)) {
            ThemeStore.editTheme(this)
                    .accentColorRes(R.color.accent_color)
                    .coloredNavigationBar(true)
                    .commit()
        }

        if (VersionUtils.hasNougatMR())
            DynamicShortcutManager(this).initDynamicShortcuts()
    }

    companion object {
        private var instance: App? = null

        fun getContext(): App {
            return instance!!
        }

        lateinit var musicComponent: MusicComponent

    }
}
