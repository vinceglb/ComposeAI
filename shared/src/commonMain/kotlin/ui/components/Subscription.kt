package ui.components

import androidx.compose.runtime.Composable

expect class SubscriptionState {
    fun launchBillingFlow()
}

@Composable
expect fun rememberSubscriptionState(): SubscriptionState
