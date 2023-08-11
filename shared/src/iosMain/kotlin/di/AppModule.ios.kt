package di

import analytics.FirebaseAnalyticsHelper
import analytics.StubAnalyticsHelper
import data.database.AppDatabase
import data.database.DriverFactory
import data.local.SettingsFactory
import data.repository.BillingRepository
import expect.isDebug
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual fun sharedPlatformModule(): Module = module {
    singleOf(::SettingsFactory)
    single { AppDatabase.getDatabase(DriverFactory().createDriver()) }

    // Analytics
    single {
        when(isDebug) {
            true -> StubAnalyticsHelper()
            else -> FirebaseAnalyticsHelper()
        }
    }

    singleOf(::BillingRepository)
}
