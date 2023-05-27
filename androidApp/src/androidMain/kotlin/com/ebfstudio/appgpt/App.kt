package com.ebfstudio.appgpt

import android.app.Application
import com.ebfstudio.appgpt.common.BuildConfig
import com.google.android.gms.ads.MobileAds
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
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

        // Init Firebase Analytics & Crashlytics in production only
        if (BuildConfig.DEBUG.not()) {
            Firebase.analytics.setAnalyticsCollectionEnabled(true)
            Firebase.crashlytics.setCrashlyticsCollectionEnabled(true)
        }

        // Init Koin
        initKoin {
            androidContext(this@App)
        }

        // AdMob
        MobileAds.initialize(this)
    }

}
