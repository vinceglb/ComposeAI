package di

import com.aallam.openai.client.OpenAI
import com.myapplication.common.BuildKonfig
import data.local.ChatMessageLocalDataSource
import data.local.SettingsFactory
import data.repository.ChatMessageRepository
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import ui.ChatScreenModel

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        modules(commonModule, sharedPlatformModule())
    }
}

// for ios
fun initKoin() {
    initKoin {  }
}

val commonModule = module {
    // ScreenModels
    factoryOf(::ChatScreenModel)

    // Repositories
    singleOf(::ChatMessageRepository)

    // DataSources
    factoryOf(::ChatMessageLocalDataSource)

    // Others
    single { OpenAI(BuildKonfig.OPENAI_API_KEY) }
    single {
        val factory: SettingsFactory = get()
        factory.createSettings()
    }
}

expect fun sharedPlatformModule(): Module
