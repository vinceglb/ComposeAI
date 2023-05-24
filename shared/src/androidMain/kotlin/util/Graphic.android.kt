package util

import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath

actual fun Path.asPlatformPathRewind(): Unit {
    this.asAndroidPath().rewind()
}
