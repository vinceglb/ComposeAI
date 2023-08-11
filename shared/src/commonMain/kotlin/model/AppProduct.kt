package model

data class AppProduct(
    val productId: String,
    val name: String,
    val title: String,
    val description: String,
    val productType: String,
    val offers: List<AppProductOffer>,
)

data class AppProductOffer(
    val offerId: String?,
    val basePlanId: String,
    val offerToken: String,
    val pricing: List<AppPricing>
)

data class AppPricing(
    val formattedPrice: String,
    val priceCurrencyCode: String,
)
