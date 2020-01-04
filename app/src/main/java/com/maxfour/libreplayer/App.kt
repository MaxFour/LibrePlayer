package com.maxfour.libreplayer

import androidx.multidex.MultiDexApplication
import com.maxfour.appthemehelper.ThemeStore
import com.maxfour.appthemehelper.util.VersionUtils
import com.maxfour.libreplayer.appshortcuts.DynamicShortcutManager
import com.maxfour.libreplayer.dagger.DaggerMusicComponent
import com.maxfour.libreplayer.dagger.MusicComponent
import com.maxfour.libreplayer.dagger.module.AppModule

class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        musicComponent = initDagger(this)

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

    private fun initDagger(app: App): MusicComponent =
            DaggerMusicComponent.builder()
                    .appModule(AppModule(app))
                    .build()

    companion object {
        private var instance: App? = null

        fun getContext(): App {
            return instance!!
        }

        lateinit var musicComponent: MusicComponent
    }
}
