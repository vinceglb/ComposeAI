package ui.components

import android.content.Context
import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.cache.memory.maxSizePercent
import com.seiko.imageloader.component.setupDefaultComponents
import com.seiko.imageloader.util.DebugLogger
import com.seiko.imageloader.util.LogPriority
import okio.Path.Companion.toOkioPath

lateinit var appContextForImagesMP: Context

actual fun generateImageLoader(): ImageLoader {
    return ImageLoader {
        logger = DebugLogger(LogPriority.VERBOSE)
        components {
            setupDefaultComponents(appContextForImagesMP)
        }
        interceptor {
            memoryCacheConfig {
                // Set the max size to 25% of the app's available memory.
                maxSizePercent(appContextForImagesMP, 0.25)
            }
            diskCacheConfig {
                directory(appContextForImagesMP.cacheDir.resolve("image_cache").toOkioPath())
                maxSizeBytes(512L * 1024 * 1024) // 512MB
            }
        }
    }
}