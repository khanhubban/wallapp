package wallapp.content.network.model

import kotlinx.serialization.Serializable

@Serializable
data class NetworkImageSubLayoutParams(
    val xRatio: Double,
    val yRatio: Double,
    val sceneWidthRatio: Double,
    val sceneHeightRatio: Double
)