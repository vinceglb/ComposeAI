package markdown.compose.elements

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import extendedspans.ExtendedSpans
import extendedspans.RoundedCornerSpanPainter
import extendedspans.SquigglyUnderlineSpanPainter
import extendedspans.drawBehind
import extendedspans.rememberSquigglyUnderlineAnimator
import markdown.compose.LocalMarkdownColors
import markdown.compose.LocalMarkdownTypography
import markdown.compose.LocalReferenceLinkHandler
import markdown.utils.TAG_IMAGE_URL
import markdown.utils.TAG_URL
import ui.components.ImageUrl

@Composable
internal fun MarkdownText(
    content: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalMarkdownTypography.current.text
) {
    MarkdownText(AnnotatedString(content), modifier, style)
}

@Composable
internal fun MarkdownText(
    content: AnnotatedString,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalMarkdownTypography.current.text
) {
    val uriHandler = LocalUriHandler.current
    val referenceLinkHandler = LocalReferenceLinkHandler.current
    val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }

    val hasUrl = content.getStringAnnotations(TAG_URL, 0, content.length).any()
    val textModifier = if (hasUrl) modifier.pointerInput(Unit) {
        detectTapGestures { pos ->
            layoutResult.value?.let { layoutResult ->
                val position = layoutResult.getOffsetForPosition(pos)
                content.getStringAnnotations(TAG_URL, position, position)
                    .firstOrNull()
                    ?.let { uriHandler.openUri(referenceLinkHandler.find(it.item)) }
            }
        }
    } else modifier

    ExtendedSpansText(
        text = content,
        modifier = textModifier,
        style = style,
        inlineContent = mapOf(
            TAG_IMAGE_URL to InlineTextContent(
                Placeholder(180.sp, 180.sp, PlaceholderVerticalAlign.Bottom) // TODO, identify flexible scaling!
            ) { link ->
                ImageUrl(
                    url = link,
                    contentDescription = "Markdown Image", // TODO
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        ),
        onTextLayout = { layoutResult.value = it }
    )
}

@Composable
fun ExtendedSpansText(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    inlineContent: Map<String, InlineTextContent> = mapOf(),
    onTextLayout: (TextLayoutResult) -> Unit = {},
) {
    val borderColor = LocalMarkdownColors.current.borderBlockCodeColor
    val underlineAnimator = rememberSquigglyUnderlineAnimator()
    val extendedSpans = remember {
        ExtendedSpans(
            RoundedCornerSpanPainter(
                cornerRadius = 4.sp,
                padding = RoundedCornerSpanPainter.TextPaddingValues(horizontal = 3.sp),
                topMargin = 2.sp,
                bottomMargin = 1.sp,
                stroke = RoundedCornerSpanPainter.Stroke(
                    color = borderColor.copy(alpha = 0.4f)
                ),
            ),
            SquigglyUnderlineSpanPainter(
                width = 4.sp,
                wavelength = 20.sp,
                amplitude = 2.sp,
                bottomOffset = 2.sp,
                animator = underlineAnimator
            )
        )
    }

    Text(
        modifier = modifier.drawBehind(extendedSpans),
        text = remember(text) {
            extendedSpans.extend(text)
        },
        onTextLayout = { result ->
            extendedSpans.onTextLayout(result)
            onTextLayout(result)
        },
        style = style,
        inlineContent = inlineContent,
    )
}