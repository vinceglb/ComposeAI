package data.repository

import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.PurchasesDelegate
import com.revenuecat.purchases.kmp.models.CustomerInfo
import com.revenuecat.purchases.kmp.models.PurchasesError
import com.revenuecat.purchases.kmp.models.StoreProduct
import com.revenuecat.purchases.kmp.models.StoreTransaction
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BillingRepository {
    private val _isSubToUnlimited: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isSubToUnlimited: StateFlow<Boolean> = _isSubToUnlimited.asStateFlow()

    init {
        Purchases.sharedInstance.delegate = object : PurchasesDelegate {
            override fun onCustomerInfoUpdated(customerInfo: CustomerInfo) {
                Napier.d { "onCustomerInfoUpdated called with customerInfo: $customerInfo" }
                _isSubToUnlimited.value = customerInfo.entitlements.active.containsKey("unlimited")
            }

            override fun onPurchasePromoProduct(
                product: StoreProduct,
                startPurchase: ((PurchasesError, Boolean) -> Unit, (StoreTransaction, CustomerInfo) -> Unit) -> Unit
            ) {
                Napier.d { "onPurchasePromoProduct called with product: $product" }
            }
        }
    }
}
