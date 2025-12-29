package wallapp.image.thumbhash

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class ThumbHashImage(
    @SerialName("w") val width: Int,
    @SerialName("h") val height: Int,
    @SerialName("d") val rgba: ByteArray,
) {
    val aspectRatio: Float by lazy { width.toFloat() / height.toFloat() }

    val jsonString: String
        get() = Json.encodeToString(serializer(), this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ThumbHashImage

        if (width != other.width) return false
        if (height != other.height) return false
        if (!rgba.contentEquals(other.rgba)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = width
        result = 31 * result + height
        result = 31 * result + rgba.contentHashCode()
        return result
    }

    companion object {
        fun fromJson(jsonString: String): ThumbHashImage {
            return Json.decodeFromString(jsonString)
        }
    }
}
