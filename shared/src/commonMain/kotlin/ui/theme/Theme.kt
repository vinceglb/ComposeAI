package ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import kotlinx.coroutines.runBlocking

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Load Typography
    val defaultTypography = MaterialTheme.typography
    val typography: Typography = runBlocking { appTypography(defaultTypography) }
    val colorSchemeTemp = platformColorScheme(darkTheme)
    val colorScheme = colorSchemeTemp.copy()

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        shapes = Shapes,
        content = content
    )
}

@Composable
expect fun platformColorScheme(darkTheme: Boolean): ColorScheme
