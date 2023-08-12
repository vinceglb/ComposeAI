package data.local

import com.russhwolf.settings.coroutines.FlowSettings
import kotlinx.coroutines.flow.Flow

class PreferenceLocalDataSource(
    private val settings: FlowSettings
) {

    // GETTERS

    suspend fun welcomeShown(): Boolean =
        settings.getBoolean(WELCOME_SHOWN, false)

    fun inAppReviewShown(): Flow<Boolean> =
        settings.getBooleanFlow(IN_APP_REVIEW_SHOWN, false)

    fun coins(): Flow<Int> =
        settings.getIntFlow(COINS, 5)

    // SETTERS

    suspend fun setWelcomeShown() {
        settings.putBoolean(WELCOME_SHOWN, true)
    }

    suspend fun setInAppReviewShown() {
        settings.putBoolean(IN_APP_REVIEW_SHOWN, true)
    }

    suspend fun addTokens(tokens: Int): Int {
        val currentTokens = settings.getInt(TOKENS_TOTAL, 0)
        val newTokens = currentTokens + tokens
        settings.putInt(TOKENS_TOTAL, newTokens)
        return newTokens
    }

    suspend fun incrementMessages(): Int {
        val currentMessages = settings.getInt(MESSAGES_TOTAL, 0)
        val newMessages = currentMessages + 1
        settings.putInt(MESSAGES_TOTAL, newMessages)
        return newMessages
    }

    suspend fun setCoins(coins: Int) {
        settings.putInt(COINS, coins)
    }

    companion object {
        private const val WELCOME_SHOWN = "welcome_shown"
        private const val IN_APP_REVIEW_SHOWN = "in_app_review_shown"
        private const val TOKENS_TOTAL = "tokens_total"
        private const val MESSAGES_TOTAL = "messages_total"
        private const val COINS = "coins"
    }

}
