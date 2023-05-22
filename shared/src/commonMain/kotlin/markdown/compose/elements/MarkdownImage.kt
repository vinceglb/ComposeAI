package markdown.compose.elements

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import markdown.utils.findChildOfTypeRecursive
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode
import ui.components.ImageUrl

@Composable
internal fun MarkdownImage(content: String, node: ASTNode) {
    val link = node.findChildOfTypeRecursive(MarkdownElementTypes.LINK_DESTINATION)?.getTextInNode(content)?.toString() ?: return
    ImageUrl(
        url = link,
        contentDescription = "Markdown Image", // TODO
        contentScale = ContentScale.FillWidth,
        modifier = Modifier.fillMaxWidth()
    )
}
