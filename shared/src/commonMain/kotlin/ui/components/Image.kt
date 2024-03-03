package ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage

@Composable
internal fun ImageUrl(
    url: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    contentDescription: String? = null,
) {
    AsyncImage(
        url,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
    )
}
