package di

import AppScreenModel
import com.aallam.openai.client.OpenAI
import com.ebfstudio.appgpt.common.BuildKonfigCommon
import com.ebfstudio.appgpt.common.Database
import data.local.PreferenceLocalDataSource
import data.local.SettingsFactory
import data.repository.BillingRepository
import data.repository.ChatMessageRepository
import data.repository.ChatRepository
import data.repository.CoinRepository
import data.repository.PreferenceRepository
import io.ktor.utils.io.core.String
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import ui.screens.bank.BankViewModel
import ui.screens.chat.ChatScreenModel
import ui.screens.welcome.WelcomeScreenModel
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

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

@OptIn(ExperimentalEncodingApi::class)
val commonModule = module {
    // ScreenModels
    factoryOf(::AppScreenModel)
    factoryOf(::WelcomeScreenModel)
    factory { params -> ChatScreenModel(get(), get(), get(), get(), get(), get(), initialChatId = params.getOrNull()) }
    factoryOf(::BankViewModel)

    // Repositories
    singleOf(::ChatRepository)
    singleOf(::ChatMessageRepository)
    singleOf(::PreferenceRepository)
    singleOf(::CoinRepository)
    singleOf(::BillingRepository)

    // DataSources
    factoryOf(::PreferenceLocalDataSource)

    // Databases
    factory { get<Database>().chatEntityQueries }
    factory { get<Database>().chatMessageEntityQueries }

    // Others
    factory { Dispatchers.Default }
    single { OpenAI(String(Base64.decode(BuildKonfigCommon.OPENAI_API_KEY))) }
    single {
        val factory: SettingsFactory = get()
        factory.createSettings()
    }
    factory { CoroutineScope(SupervisorJob() + Dispatchers.Default) }
}

expect fun sharedPlatformModule(): Module
