package ui.components

import androidx.compose.runtime.Composable

expect class InAppReviewState {
    val onComplete: () -> Unit
    fun show()
}

@Composable
expect fun rememberInAppReviewState(onComplete: () -> Unit): InAppReviewState
