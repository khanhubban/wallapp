package wallapp.content.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkPurchasableProductIds(
    @SerialName("appStore") val appStoreProductId: String,
    @SerialName("playStore") val googlePlayProductId: String,
    @SerialName("revenueCat") val revenueCatEntitlementId: String,
)
