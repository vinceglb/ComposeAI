package di

import analytics.FirebaseAnalyticsHelper
import analytics.StubAnalyticsHelper
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import data.database.AppDatabase
import data.database.DriverFactory
import data.local.SettingsFactory
import expect.isDebug
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual fun sharedPlatformModule(): Module = module {
    singleOf(::SettingsFactory)
    single {
        val driver = DriverFactory(get()).createDriver()
        AppDatabase.getDatabase(driver)
    }

    // Firebase
    single { Firebase.analytics }

    // Analytics
    single {
        when(isDebug) {
            true -> StubAnalyticsHelper()
            else -> FirebaseAnalyticsHelper(get())
        }
    }
}
