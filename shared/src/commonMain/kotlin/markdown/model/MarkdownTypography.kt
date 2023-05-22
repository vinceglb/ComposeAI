package markdown.model

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle

interface MarkdownTypography {
    val text: TextStyle
    val code: TextStyle
    val h1: TextStyle
    val h2: TextStyle
    val h3: TextStyle
    val h4: TextStyle
    val h5: TextStyle
    val h6: TextStyle
    val quote: TextStyle
    val paragraph: TextStyle
    val ordered: TextStyle
    val bullet: TextStyle
    val list: TextStyle
}

@Immutable
private class DefaultMarkdownTypography(
    override val h1: TextStyle,
    override val h2: TextStyle,
    override val h3: TextStyle,
    override val h4: TextStyle,
    override val h5: TextStyle,
    override val h6: TextStyle,
    override val text: TextStyle,
    override val code: TextStyle,
    override val quote: TextStyle,
    override val paragraph: TextStyle,
    override val ordered: TextStyle,
    override val bullet: TextStyle,
    override val list: TextStyle
) : MarkdownTypography

@Composable
fun markdownTypography(
    h1: TextStyle = MaterialTheme.typography.headlineLarge,
    h2: TextStyle = MaterialTheme.typography.headlineMedium,
    h3: TextStyle = MaterialTheme.typography.headlineSmall,
    h4: TextStyle = MaterialTheme.typography.titleLarge,
    h5: TextStyle = MaterialTheme.typography.titleMedium,
    h6: TextStyle = MaterialTheme.typography.titleSmall,
    text: TextStyle = MaterialTheme.typography.bodyLarge,
    code: TextStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
    quote: TextStyle = MaterialTheme.typography.bodySmall.plus(SpanStyle(fontStyle = FontStyle.Italic)),
    paragraph: TextStyle = MaterialTheme.typography.bodyLarge,
    ordered: TextStyle = MaterialTheme.typography.bodyLarge,
    bullet: TextStyle = MaterialTheme.typography.bodyLarge,
    list: TextStyle = MaterialTheme.typography.bodyLarge
): MarkdownTypography = DefaultMarkdownTypography(
    h1 = h1, h2 = h2, h3 = h3, h4 = h4, h5 = h5, h6 = h6,
    text = text, quote = quote, code = code, paragraph = paragraph,
    ordered = ordered, bullet = bullet, list = list
)
