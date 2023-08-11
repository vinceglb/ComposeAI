package ui.components

import androidx.compose.runtime.Composable

actual class AdsState(
    actual val onRewardEarned: (Int) -> Unit
) {
    actual fun show() {
        TODO()
    }

    actual val isLoaded: Boolean
        get() = TODO("Not yet implemented")
}

@Composable
actual fun rememberAdsState(
    onRewardEarned: (Int) -> Unit,
): AdsState = TODO()
