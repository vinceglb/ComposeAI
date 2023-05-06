package ui.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.LocalImageLoader
import com.seiko.imageloader.rememberAsyncImagePainter

@Composable
internal fun ImageUrl(
    url: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    contentDescription: String? = null,
) {
    CompositionLocalProvider(
        LocalImageLoader provides generateImageLoader(),
    ) {
        Image(
            painter = rememberAsyncImagePainter(url),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale,
        )
    }
}

expect fun generateImageLoader(): ImageLoader

