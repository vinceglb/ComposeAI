package data.local

import com.russhwolf.settings.coroutines.FlowSettings

expect class SettingsFactory {
    fun createSettings(): FlowSettings
}
