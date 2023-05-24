package markdown.model

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Stable
interface MarkdownColors {
    /** Represents the color used for the text of this [Markdown] component. */
    val text: Color

    /** Represents the background color for this [Markdown] component. */
    val backgroundCode: Color

    /** Represents the color used for the border of this [Markdown] component. */
    val borderBlockCodeColor: Color
}

@Immutable
private class DefaultMarkdownColors(
    override val text: Color,
    override val backgroundCode: Color,
    override val borderBlockCodeColor: Color
) : MarkdownColors

@Composable
fun markdownColor(
    text: Color = MaterialTheme.colorScheme.onBackground,
    backgroundCode: Color = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
    borderBlockCodeColor: Color = MaterialTheme.colorScheme.surfaceVariant,
): MarkdownColors = DefaultMarkdownColors(
    text = text,
    backgroundCode = backgroundCode,
    borderBlockCodeColor = borderBlockCodeColor
)
