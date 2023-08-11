package ui.components

import androidx.compose.runtime.Composable

expect class AdsState {
    val onRewardEarned: (Int) -> Unit
    val isLoaded: Boolean

    fun show()
}

@Composable
expect fun rememberAdsState(
    onRewardEarned: (Int) -> Unit,
): AdsState
