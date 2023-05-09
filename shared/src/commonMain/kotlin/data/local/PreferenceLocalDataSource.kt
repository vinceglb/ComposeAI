package data.local

import com.russhwolf.settings.coroutines.FlowSettings
import kotlinx.coroutines.flow.Flow

class PreferenceLocalDataSource(
    private val settings: FlowSettings
) {

    fun welcomeShown(): Flow<Boolean> =
        settings.getBooleanFlow(WELCOME_SHOWN, false)

    suspend fun setWelcomeShown() {
        settings.putBoolean(WELCOME_SHOWN, true)
    }

    companion object {
        private const val WELCOME_SHOWN = "welcome_shown"
    }

}
