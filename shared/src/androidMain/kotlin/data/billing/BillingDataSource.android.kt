package data.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.acknowledgePurchase
import com.android.billingclient.api.queryProductDetails
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// https://github.com/android/play-billing-samples/tree/main
// https://github.com/android/play-billing-samples/blob/main/ClassyTaxiAppKotlin/app/src/main/java/com/example/billing/gpbl/BillingClientLifecycle.kt
actual class BillingDataSource(
    context: Context,
    private val externalScope: CoroutineScope,
) : PurchasesUpdatedListener,
    BillingClientStateListener {

    private val billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases()
        .build()

    private val _subscriptionPurchases = MutableStateFlow<List<Purchase>>(emptyList())

    private val _unlimitedSubProduct: MutableStateFlow<ProductDetails?> = MutableStateFlow(null)

    /**
     * Cached in-app product purchases details.
     */
    private var cachedPurchasesList: List<Purchase>? = null


    // val billingState: StateFlow<BillingState> = _billingState.asStateFlow()

    init {
        connect()
    }

    fun connect() {
        if (!billingClient.isReady) {
            Napier.d("BillingClient: Start connection...")
            billingClient.startConnection(this)
        }
    }

    override fun onBillingSetupFinished(billingResult: BillingResult) {
        val responseCode = billingResult.responseCode
        val debugMessage = billingResult.debugMessage
        Napier.d("onBillingSetupFinished: $responseCode $debugMessage")
        if (responseCode == BillingResponseCode.OK) {
            // The billing client is ready.
            // You can query product details and purchases here.
            externalScope.launch {
                querySubscriptionProductDetails()
            }
        }
    }

    override fun onBillingServiceDisconnected() {
        Napier.w { "onBillingServiceDisconnected" }
        // TODO: Try connecting again with exponential backoff.
        // billingClient.startConnection(this)
    }

    /**
     * In order to make purchases, you need the [ProductDetails] for the item or subscription.
     * This is an asynchronous call that will receive a result in [onProductDetailsResponse].
     *
     * querySubscriptionProductDetails uses method calls from GPBL 5.0.0. PBL5, released in May 2022,
     * is backwards compatible with previous versions.
     * To learn more about this you can read:
     * https://developer.android.com/google/play/billing/compatibility
     */
    private suspend fun querySubscriptionProductDetails() {
        Napier.d { "querySubscriptionProductDetails" }
        val params = QueryProductDetailsParams.newBuilder()

        val productList: MutableList<QueryProductDetailsParams.Product> = arrayListOf()
        for (product in LIST_OF_SUBSCRIPTION_PRODUCTS) {
            productList.add(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(product)
                    .setProductType(ProductType.SUBS)
                    .build()
            )
        }

        // Query product details
        val productDetailsParams = params.setProductList(productList)
        val (billingResult, productDetailsList) = withContext(Dispatchers.IO) {
            billingClient.queryProductDetails(productDetailsParams.build())
        }

        val responseCode = billingResult.responseCode
        val debugMessage = billingResult.debugMessage
        Napier.d("queryProductDetails: $responseCode $debugMessage")

        when (responseCode) {
            BillingResponseCode.OK -> {
                // Process the result
                productDetailsList?.let { processProductDetails(it) }
            }

            else -> {
                // TODO
            }
        }
    }

    /**
     * This method is used to process the product details list returned by the [BillingClient]and
     * post the details to the [basicSubProductWithProductDetails] and
     * [premiumSubProductWithProductDetails] live data.
     *
     * @param productDetailsList The list of product details.
     *
     */
    private suspend fun processProductDetails(productDetailsList: List<ProductDetails>) {
        val expectedProductDetailsCount = LIST_OF_SUBSCRIPTION_PRODUCTS.size
        if (productDetailsList.isEmpty()) {
            Napier.e {
                "processProductDetails: " +
                        "Expected ${expectedProductDetailsCount}, " +
                        "Found null ProductDetails. " +
                        "Check to see if the products you requested are correctly published " +
                        "in the Google Play Console."
            }
            postProductDetails(emptyList())
        } else {
            postProductDetails(productDetailsList)
        }
    }

    /**
     * This method is used to post the product details to the [basicSubProductWithProductDetails]
     * and [premiumSubProductWithProductDetails] live data.
     *
     * @param productDetailsList The list of product details.
     *
     */
    private suspend fun postProductDetails(productDetailsList: List<ProductDetails>) {
        productDetailsList.forEach { productDetails ->
            when (productDetails.productType) {
                ProductType.SUBS -> {
                    when (productDetails.productId) {
                        UNLIMITED_MESSAGES_SUBSCRIPTION -> {
                            _unlimitedSubProduct.emit(productDetails)
                        }
                    }
                }

                ProductType.INAPP -> {}
            }
        }
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        val responseCode = billingResult.responseCode
        val debugMessage = billingResult.debugMessage
        Napier.d("onPurchasesUpdated: $responseCode $debugMessage")
        when (responseCode) {
            BillingResponseCode.OK -> {
                if (purchases == null) {
                    Napier.d("onPurchasesUpdated: null purchase list")
                    processPurchases(null)
                } else {
                    processPurchases(purchases)
                }
            }

            BillingResponseCode.USER_CANCELED -> {
                Napier.i("onPurchasesUpdated: User canceled the purchase")
            }

            BillingResponseCode.ITEM_ALREADY_OWNED -> {
                Napier.i("onPurchasesUpdated: The user already owns this item")
            }

            BillingResponseCode.DEVELOPER_ERROR -> {
                Napier.e(
                    "onPurchasesUpdated: Developer error means that Google Play does " +
                            "not recognize the configuration. If you are just getting started, " +
                            "make sure you have configured the application correctly in the " +
                            "Google Play Console. The product ID must match and the APK you " +
                            "are using must be signed with release keys."
                )
            }
        }
    }

    /**
     * Send purchase to StateFlow, which will trigger network call to verify the subscriptions
     * on the sever.
     */
    private fun processPurchases(purchasesList: List<Purchase>?) {
        Napier.d { "processPurchases: ${purchasesList?.size} purchase(s)" }
        purchasesList?.let { list ->
            if (isUnchangedPurchaseList(list)) {
                Napier.d { "processPurchases: Purchase list has not changed" }
                return
            }
            externalScope.launch {
                val subscriptionPurchaseList = list.filter { purchase ->
                    purchase.products.any { product ->
                        product in LIST_OF_SUBSCRIPTION_PRODUCTS
                    }
                }

                _subscriptionPurchases.emit(subscriptionPurchaseList)
            }
            logAcknowledgementStatus(list)
        }
    }

    /**
     * Check whether the purchases have changed before posting changes.
     */
    private fun isUnchangedPurchaseList(purchasesList: List<Purchase>): Boolean {
        val isUnchanged = purchasesList == cachedPurchasesList
        if (!isUnchanged) {
            cachedPurchasesList = purchasesList
        }
        return isUnchanged
    }

    /**
     * Log the number of purchases that are acknowledge and not acknowledged.
     *
     * https://developer.android.com/google/play/billing/billing_library_releases_notes#2_0_acknowledge
     *
     * When the purchase is first received, it will not be acknowledge.
     * This application sends the purchase token to the server for registration. After the
     * purchase token is registered to an account, the Android app acknowledges the purchase token.
     * The next time the purchase list is updated, it will contain acknowledged purchases.
     */
    private fun logAcknowledgementStatus(purchasesList: List<Purchase>) {
        var acknowledgedCounter = 0
        var unacknowledgedCounter = 0
        for (purchase in purchasesList) {
            if (purchase.isAcknowledged) {
                acknowledgedCounter++
            } else {
                unacknowledgedCounter++
            }
        }
        Napier.d {
            "logAcknowledgementStatus: acknowledged=$acknowledgedCounter " +
                    "unacknowledged=$unacknowledgedCounter"
        }
    }

    /**
     * Launching the billing flow.
     *
     * Launching the UI to make a purchase requires a reference to the Activity.
     */
    fun launchBillingFlow(activity: Activity, params: BillingFlowParams): Int {
        if (!billingClient.isReady) {
            Napier.e("launchBillingFlow: BillingClient is not ready")
        }
        val billingResult = billingClient.launchBillingFlow(activity, params)
        val responseCode = billingResult.responseCode
        val debugMessage = billingResult.debugMessage
        Napier.d("launchBillingFlow: BillingResponse $responseCode $debugMessage")
        return responseCode
    }

    /**
     * Acknowledge a purchase.
     *
     * https://developer.android.com/google/play/billing/billing_library_releases_notes#2_0_acknowledge
     *
     * Apps should acknowledge the purchase after confirming that the purchase token
     * has been associated with a user. This app only acknowledges purchases after
     * successfully receiving the subscription data back from the server.
     *
     * Developers can choose to acknowledge purchases from a server using the
     * Google Play Developer API. The server has direct access to the user database,
     * so using the Google Play Developer API for acknowledgement might be more reliable.
     * TODO(134506821): Acknowledge purchases on the server.
     * TODO: Remove client side purchase acknowledgement after removing the associated tests.
     * If the purchase token is not acknowledged within 3 days,
     * then Google Play will automatically refund and revoke the purchase.
     * This behavior helps ensure that users are not charged for subscriptions unless the
     * user has successfully received access to the content.
     * This eliminates a category of issues where users complain to developers
     * that they paid for something that the app is not giving to them.
     */
    suspend fun acknowledgePurchase(purchaseToken: String): Boolean {
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchaseToken)
            .build()

        return withContext(Dispatchers.IO) {
            val res = billingClient.acknowledgePurchase(params)
            Napier.i { "acknowledgePurchase: $res" }
            res.responseCode == BillingResponseCode.OK
        }
    }

    companion object {
        private const val UNLIMITED_MESSAGES_SUBSCRIPTION = "unlimited_messages"

        private val LIST_OF_SUBSCRIPTION_PRODUCTS = listOf(
            UNLIMITED_MESSAGES_SUBSCRIPTION
        )
    }

}
