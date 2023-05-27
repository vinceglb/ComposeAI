package analytics

import androidx.compose.runtime.Composable

@Composable
expect fun AdMobButton(
    tokens: Int,
    onRewardEarned: (Int) -> Unit,
)
