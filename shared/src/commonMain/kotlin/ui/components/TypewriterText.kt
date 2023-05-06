package ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import kotlinx.coroutines.delay

@Composable
fun TypewriterText(
    text: String,
    modifier: Modifier = Modifier,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
) {
    var targetText by remember { mutableStateOf(text) }
    var currentText by remember { mutableStateOf("") }

    LaunchedEffect(text) {
        if (targetText != text) {
            for (i in currentText.length - 1 downTo 0) {
                delay(50)
                currentText = currentText.substring(0, i)
            }
            targetText = text
        }

        for (i in currentText.length until text.length) {
            delay(50)
            currentText += text[i]
        }
    }

    Text(
        currentText,
        overflow = overflow,
        softWrap = softWrap,
        modifier = modifier,
    )
}
