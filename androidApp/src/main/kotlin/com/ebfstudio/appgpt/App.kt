package com.ebfstudio.appgpt

import analytics.CrashlyticsAntilog
import android.app.Application
import com.ebfstudio.appgpt.common.BuildConfig
import com.ebfstudio.appgpt.common.BuildKonfigCommon
import com.google.android.gms.ads.MobileAds
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.revenuecat.purchases.kmp.LogLevel
import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.configure
import di.initKoin
import expect.appContextForImagesMP
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        appContextForImagesMP = this@App

        // Init App Check
        FirebaseApp.initializeApp(applicationContext)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
        )

        // Init Firebase Analytics & Crashlytics in production only
        val enabled = BuildConfig.DEBUG.not()
        Firebase.analytics.setAnalyticsCollectionEnabled(enabled)
        Firebase.crashlytics.isCrashlyticsCollectionEnabled = enabled

        // Init Napier
        val antilog = if (BuildConfig.DEBUG) DebugAntilog() else CrashlyticsAntilog()
        Napier.base(antilog)

        // Init RevenueCat
        Purchases.logLevel = LogLevel.DEBUG
        Purchases.configure(apiKey = BuildKonfigCommon.REVENUECAT_API_KEY)

        // Init Koin
        initKoin {
            androidContext(this@App)
        }

        // AdMob
        MobileAds.initialize(this)
    }

}
