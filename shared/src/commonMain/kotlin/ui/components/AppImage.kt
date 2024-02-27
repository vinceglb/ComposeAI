package ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import composeai.shared.generated.resources.Res
import composeai.shared.generated.resources.ic_launcher_light_playstore
import composeai.shared.generated.resources.ic_launcher_playstore
import org.jetbrains.compose.resources.DrawableResource

@Composable
fun appImagePath(): DrawableResource {
    return when(isSystemInDarkTheme()) {
        true -> Res.drawable.ic_launcher_playstore
        else -> Res.drawable.ic_launcher_light_playstore
    }
}
