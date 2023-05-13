package com.ebfstudio.appgpt

import android.app.Application
import di.initKoin
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import ui.components.appContextForImagesMP

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        appContextForImagesMP = this@App

        // Init Napier
        Napier.base(DebugAntilog())

        // Init Koin
        initKoin {
            androidContext(this@App)
        }
    }

}
