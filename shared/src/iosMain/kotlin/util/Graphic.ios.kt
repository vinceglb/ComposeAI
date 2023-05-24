package util

import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asSkiaPath

actual fun Path.asPlatformPathRewind() {
    this.asSkiaPath().rewind()
}
