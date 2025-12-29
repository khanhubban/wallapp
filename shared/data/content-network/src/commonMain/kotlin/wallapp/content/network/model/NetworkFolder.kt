package wallapp.content.network.model

import kotlinx.serialization.Serializable

@Serializable
data class NetworkFolder(
    val id: String,
    val title: String,
    val titleTwoLines: String = title,
    val remixIds: List<String>,
    val collectionIds: List<String>? = null,
    val profileImage: NetworkMedia,
    val featureBannerImage: NetworkMedia,
)
