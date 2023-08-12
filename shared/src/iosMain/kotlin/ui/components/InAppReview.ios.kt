package ui.components

import androidx.compose.runtime.Composable

actual class InAppReviewState(
    actual val onComplete: () -> Unit,
    actual val onError: () -> Unit,
) {
    actual fun show() {
        TODO()
    }
}

@Composable
actual fun rememberInAppReviewState(
    onComplete: () -> Unit,
    onError: () -> Unit,
): InAppReviewState {
    return InAppReviewState(onComplete, onError)
}
