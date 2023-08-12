package data.repository

import analytics.AnalyticsHelper
import analytics.logWelcomeSeen
import data.local.PreferenceLocalDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class PreferenceRepository(
    private val preferenceLocalDataSource: PreferenceLocalDataSource,
    private val analyticsHelper: AnalyticsHelper,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
) {

    suspend fun welcomeShown(): Boolean =
        withContext(defaultDispatcher) {
            preferenceLocalDataSource.welcomeShown()
        }

    fun inAppReviewShown(): Flow<Boolean> =
        preferenceLocalDataSource.inAppReviewShown()

    suspend fun setWelcomeShown(): Unit =
        withContext(defaultDispatcher) {
            preferenceLocalDataSource.setWelcomeShown()
            analyticsHelper.logWelcomeSeen()
        }

    suspend fun setInAppReviewShown(): Unit =
        withContext(defaultDispatcher) {
            preferenceLocalDataSource.setInAppReviewShown()
        }

}
