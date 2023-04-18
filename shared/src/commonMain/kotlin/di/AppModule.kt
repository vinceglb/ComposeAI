package di

import com.aallam.openai.client.OpenAI
import data.ChatRepository
import data.FakeRepository
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import ui.ChatScreenModel

fun initKoin() {
    startKoin {
        modules(commonModule)
    }
}

val commonModule = module {
    factoryOf(::ChatScreenModel)
    singleOf(::FakeRepository)
    single { OpenAI("...") }
    singleOf(::ChatRepository)
}
