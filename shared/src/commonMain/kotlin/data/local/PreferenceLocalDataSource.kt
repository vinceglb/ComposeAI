package data.local

import com.russhwolf.settings.coroutines.FlowSettings
import kotlinx.coroutines.flow.Flow

class PreferenceLocalDataSource(
    private val settings: FlowSettings
) {

    // GETTERS

    suspend fun welcomeShown(): Boolean =
        settings.getBoolean(WELCOME_SHOWN, false)

    fun tokens(): Flow<Int> =
        settings.getIntFlow(TOKENS, DEFAULT_TOKENS)

    // SETTERS

    suspend fun setWelcomeShown() {
        settings.putBoolean(WELCOME_SHOWN, true)
    }

    suspend fun setTokens(tokens: Int) {
        settings.putInt(TOKENS, tokens)
    }

    companion object {
        private const val WELCOME_SHOWN = "welcome_shown"
        private const val TOKENS = "tokens"

        private const val DEFAULT_TOKENS = 10
    }

}
