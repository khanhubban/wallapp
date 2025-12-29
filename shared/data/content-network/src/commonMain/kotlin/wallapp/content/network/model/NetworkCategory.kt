package wallapp.content.network.model

import kotlinx.serialization.Serializable

@Serializable
data class NetworkCategory(
    val id: String,
    val label: String,
    val artistId: String,
    val featureBannerImage: NetworkMedia? = null,
    val categoryType: String,
    val previewRemixId: String,
    val remixIds: List<String>,
    val slugs: List<String>,
    val purchasableProductIds: NetworkPurchasableProductIds? = null,
)