package analytics

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun AdMobButton(
    coins: Int,
    onRewardEarned: (Int) -> Unit,
    modifier: Modifier,
) {}
