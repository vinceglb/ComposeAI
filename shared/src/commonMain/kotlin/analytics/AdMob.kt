package analytics

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun AdMobButton(
    coins: Int,
    onRewardEarned: (Int) -> Unit,
    modifier: Modifier = Modifier,
)
