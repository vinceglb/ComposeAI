package com.ebfstudio.appgpt

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
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

        // Init App Check
        FirebaseApp.initializeApp(applicationContext)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
        )

        // Init Koin
        initKoin {
            androidContext(this@App)
        }
    }

}
