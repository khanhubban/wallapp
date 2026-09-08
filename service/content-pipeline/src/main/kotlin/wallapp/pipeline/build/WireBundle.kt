package wallapp.pipeline.build

import wallapp.content.network.model.NetworkContent
import wallapp.media.network.model.NetworkMediaData
import wallapp.search.model.NetworkSearchMetadata

data class WireBundle(
    val content: NetworkContent,
    val search: NetworkSearchMetadata,
    val media: NetworkMediaData,
    val baseUrl: String,
    val imgixHostPrefix: String,
)
