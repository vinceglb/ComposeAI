package data.repository

import data.local.PreferenceLocalDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class TokenRepository(
    private val preferences: PreferenceLocalDataSource,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    fun tokens(): Flow<Int> =
        preferences.tokens()

    suspend fun useTokens(remove: Int = 0, add: Int = 0): Unit =
        withContext(defaultDispatcher) {
            val tokens = preferences.tokens().first()
            var newTokens = tokens + add - remove

            // Prevent negative tokens
            if (newTokens < 0) {
                newTokens = 0
            }

            preferences.setTokens(newTokens)
        }
}
