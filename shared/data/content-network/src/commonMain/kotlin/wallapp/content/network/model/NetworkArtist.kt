package wallapp.content.network.model

import kotlinx.serialization.Serializable

@Serializable
data class NetworkArtist(
    val id: String,
    val label: String,
    val profileImage: NetworkMedia,
    val featureBannerImage: NetworkMedia? = null,
    val slugs: List<String>,
    val categoryIds: List<String>,
    val socialLinks: NetworkSocialLinks,
)
