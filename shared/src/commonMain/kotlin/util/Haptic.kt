package util

import androidx.compose.runtime.Composable
import androidx.compose.ui.hapticfeedback.HapticFeedback

@Composable
expect fun rememberHapticFeedback() : HapticFeedback
