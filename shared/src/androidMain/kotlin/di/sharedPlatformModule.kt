package di

import data.database.AppDatabase
import data.database.DriverFactory
import data.local.SettingsFactory
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual fun sharedPlatformModule(): Module = module {
    singleOf(::SettingsFactory)
    single {
        val driver = DriverFactory(get()).createDriver()
        AppDatabase.getDatabase(driver)
    }
}
