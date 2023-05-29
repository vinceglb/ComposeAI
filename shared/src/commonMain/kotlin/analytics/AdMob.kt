package analytics

import androidx.compose.runtime.Composable

@Composable
expect fun AdMobButton(
    coins: Int,
    onRewardEarned: (Int) -> Unit,
)
