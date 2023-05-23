package expect

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import model.AppPlatform

expect fun platform(): AppPlatform

@Composable
expect fun ChatTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
)
