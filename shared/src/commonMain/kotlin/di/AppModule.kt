package di

import com.aallam.openai.client.OpenAI
import com.ebfstudio.appgpt.common.BuildKonfig
import com.ebfstudio.appgpt.common.Database
import data.local.ChatMessageLocalDataSource
import data.local.SettingsFactory
import data.repository.ChatMessageRepository
import data.repository.ChatRepository
import kotlinx.coroutines.Dispatchers
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import ui.screens.chat.ChatScreenModel

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        modules(commonModule, sharedPlatformModule())
    }
}

// for ios
@Suppress("unused")
fun initKoin() {
    initKoin {  }
}

val commonModule = module {
    // ScreenModels
    factory { params -> ChatScreenModel(get(), get(), initialChatId = params.getOrNull()) }

    // Repositories
    singleOf(::ChatRepository)
    singleOf(::ChatMessageRepository)

    // DataSources
    factoryOf(::ChatMessageLocalDataSource)

    // Databases
    factory { get<Database>().chatEntityQueries }
    factory { get<Database>().chatMessageEntityQueries }

    // Others
    factory { Dispatchers.Default }
    single { OpenAI(BuildKonfig.OPENAI_API_KEY) }
    single {
        val factory: SettingsFactory = get()
        factory.createSettings()
    }
}

expect fun sharedPlatformModule(): Module
