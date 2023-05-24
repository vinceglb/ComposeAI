package util

import androidx.compose.runtime.Composable
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.platform.LocalHapticFeedback

@Composable
actual fun rememberHapticFeedback(): HapticFeedback {
    return LocalHapticFeedback.current
}
