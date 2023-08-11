package data.repository

import com.android.billingclient.api.Purchase.PurchaseState
import data.billing.BillingDataSource
import data.billing.toAppProduct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import model.AppProduct

actual class BillingRepository(
    billingDataSource: BillingDataSource,
    externalScope: CoroutineScope,
) {
    actual val unlimitedSubProduct: StateFlow<AppProduct?> =
        billingDataSource.unlimitedSubProduct
            .map { it?.toAppProduct() }
            .stateIn(externalScope, SharingStarted.WhileSubscribed(), null)

    actual val isSubToUnlimited: StateFlow<Boolean> = combine(
        billingDataSource.unlimitedSubProduct,
        billingDataSource.subscriptionPurchases
    ) { unlimitedSubProduct, subscriptionPurchases ->
        unlimitedSubProduct != null && subscriptionPurchases.any {
            it.products.contains(unlimitedSubProduct.productId) && it.purchaseState == PurchaseState.PURCHASED
        }
    }.stateIn(externalScope, SharingStarted.WhileSubscribed(), false)
}
