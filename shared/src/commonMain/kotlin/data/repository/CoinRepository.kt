package data.repository

import data.local.PreferenceLocalDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class CoinRepository(
    private val preferences: PreferenceLocalDataSource,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    fun coins(): Flow<Int> =
        preferences.coins()

    suspend fun useCoins(remove: Int = 0, add: Int = 0): Unit =
        withContext(defaultDispatcher) {
            val tokens = preferences.coins().first()
            var newCoins = tokens + add - remove

            // Prevent negative tokens
            if (newCoins < 0) {
                newCoins = 0
            }

            preferences.setCoins(newCoins)
        }
}
