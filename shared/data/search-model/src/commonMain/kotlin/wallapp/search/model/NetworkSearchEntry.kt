package wallapp.search.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkSearchEntry(
    @SerialName("t")
    val term: String,
    @SerialName("r")
    val relevance: Float,
)