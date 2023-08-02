package ui.components

import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.cache.memory.maxSizePercent
import com.seiko.imageloader.component.setupDefaultComponents
import com.seiko.imageloader.util.DebugLogger
import com.seiko.imageloader.util.LogPriority
import kotlinx.cinterop.ExperimentalForeignApi
import okio.Path.Companion.toPath
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

actual fun generateImageLoader(): ImageLoader {
    return ImageLoader {
        logger = DebugLogger(LogPriority.VERBOSE)
        components {
            setupDefaultComponents(imageScope)
        }
        interceptor {
            memoryCacheConfig {
                // Set the max size to 25% of the app's available memory.
                maxSizePercent(0.25)
            }
            diskCacheConfig {
                directory(getCacheDir().toPath().resolve("image_cache"))
                maxSizeBytes(512L * 1024 * 1024) // 512MB
            }
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun getCacheDir(): String {
    return NSFileManager.defaultManager.URLForDirectory(
        NSCachesDirectory,
        NSUserDomainMask,
        null,
        true,
        null,
    )!!.path.orEmpty()
}