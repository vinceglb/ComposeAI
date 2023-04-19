package data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.datastore.DataStoreSettings

actual class SettingsFactory(
    private val context: Context,
) {
    actual fun createSettings(): FlowSettings =
        DataStoreSettings(context.dataStore)

    private val Context.dataStore: DataStore<Preferences>
            by preferencesDataStore(name = "settings")
}