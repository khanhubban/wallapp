package wallapp.resource

import androidx.compose.runtime.Immutable
import co.touchlab.skie.configuration.annotations.SealedInterop
import kotlinx.serialization.Serializable

@Immutable
@Serializable
@SealedInterop.Enabled
sealed class ImageHash {

    @Immutable
    @Serializable
    data class ImageHashBlur(
        val blurHash: String,
    ): ImageHash() {

        companion object {
            val Preset by lazy {
                ImageHashBlur(
                    blurHash = "UKRl5*jt8Cjtj@fQfQfQ4LfQnkfQj@fQfQfQ",
                )
            }
        }
    }

    companion object {
        fun from(model: String): ImageHash {
            return ImageHashBlur(model)
        }

        val Preset by lazy { ImageHashBlur.Preset }
    }
}