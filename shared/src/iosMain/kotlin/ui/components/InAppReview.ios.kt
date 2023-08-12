package ui.components

import androidx.compose.runtime.Composable

actual class InAppReviewState(
    actual val onComplete: () -> Unit,
) {
    actual fun show() {
        TODO()
    }
}

@Composable
actual fun rememberInAppReviewState(
    onComplete: () -> Unit
): InAppReviewState {
    return InAppReviewState(onComplete)
}
