package data.local

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import platform.Foundation.NSUserDefaults

actual class SettingsFactory {
    actual fun createSettings(): FlowSettings {
        return NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults).toFlowSettings()
    }
}
