package ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import ui.images.AppImages

@Composable
fun appImagePath(): String {
    return when(isSystemInDarkTheme()) {
        true -> AppImages.composeAIDark
        else -> AppImages.composeAILight
    }
}