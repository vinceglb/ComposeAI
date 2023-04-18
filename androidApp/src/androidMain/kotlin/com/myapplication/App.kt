package com.myapplication

import android.app.Application
import di.initKoin
import ui.components.appContextForImagesMP

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        appContextForImagesMP = this

        initKoin()
    }

}
