package ui.components

import androidx.compose.runtime.Composable

expect class InAppReviewState {
    val onComplete: () -> Unit
    val onError: () -> Unit
    fun show()
}

@Composable
expect fun rememberInAppReviewState(
    onComplete: () -> Unit = {},
    onError: () -> Unit = {},
): InAppReviewState
