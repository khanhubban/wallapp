package wallapp.content.network.model

import kotlinx.serialization.Serializable

@Serializable
data class NetworkSocialLinks(
    val facebook: String? = null,
    val instagram: String? = null,
    val shop: String? = null,
    val tiktok: String? = null,
    val twitter: String? = null,
    val website: String? = null,
    val youtube: String? = null,
)