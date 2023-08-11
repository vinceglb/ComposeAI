package ui.components

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.android.billingclient.api.BillingFlowParams
import data.billing.BillingDataSource
import kotlinx.coroutines.flow.firstOrNull
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual class SubscriptionState : KoinComponent {
    private val billingDataSource: BillingDataSource by inject()

    var doLaunch by mutableStateOf(false)

    actual fun launchBillingFlow() {
        doLaunch = true
    }

    suspend fun launchBillingFlowAndroid(activity: Activity) {
        doLaunch = false

        val productDetails = billingDataSource.unlimitedSubProduct.firstOrNull()
            ?: return

        val offerToken: String = productDetails.subscriptionOfferDetails?.firstOrNull()?.offerToken
            ?: return

        val pdp = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(productDetails)
            .setOfferToken(offerToken)
            .build()

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(pdp))
            .build()

        billingDataSource.launchBillingFlow(activity, billingFlowParams)
    }
}

@Composable
actual fun rememberSubscriptionState(): SubscriptionState {
    val subscriptionState = remember { SubscriptionState() }
    val context = LocalContext.current

    LaunchedEffect(subscriptionState.doLaunch) {
        if (subscriptionState.doLaunch) {
            subscriptionState.launchBillingFlowAndroid(context as Activity)
        }
    }

    return subscriptionState
}
