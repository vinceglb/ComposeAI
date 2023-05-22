package markdown.compose.elements

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import markdown.compose.LocalMarkdownTypography
import markdown.utils.buildMarkdownAnnotatedString
import org.intellij.markdown.ast.ASTNode

@Composable
internal fun MarkdownParagraph(
    content: String,
    node: ASTNode,
    style: TextStyle = LocalMarkdownTypography.current.paragraph
) {
    val styledText = buildAnnotatedString {
        pushStyle(style.toSpanStyle())
        buildMarkdownAnnotatedString(content, node)
        pop()
    }
    MarkdownText(styledText, style = style)
}
