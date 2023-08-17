package ui.components

import androidx.compose.runtime.Composable

actual class SubscriptionState {
    actual fun launchBillingFlow() {
        TODO()
    }
}

@Composable
actual fun rememberSubscriptionState(): SubscriptionState {
    return SubscriptionState()
}