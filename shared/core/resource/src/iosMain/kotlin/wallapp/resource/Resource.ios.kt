package wallapp.resource

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector
import co.touchlab.skie.configuration.annotations.SealedInterop
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.DrawableResource
import wallapp.graphics.Color
import wallapp.log.Log

@Serializable
@SealedInterop.Enabled
actual sealed interface Resource {

    actual val contentDescription: String?

    actual val exportString: String

    @Immutable
    @Serializable
    data class UrlImage(val url: String): Resource {
        override val contentDescription: String? = null

        override val exportString: String
            get() = Json.encodeToString(kotlinx.serialization.serializer(), this)
    }

    @Serializable
    data class UrlVideo(val url: String): Resource {
        override val contentDescription: String? = null

        override val exportString: String
            get() = Json.encodeToString(kotlinx.serialization.serializer(), this)
    }

    @Immutable
    @Serializable
    data class Vector(
        private val imageVectorWrapper: ImageVectorWrapper,
        override val contentDescription: String?,
    ): Resource {

        constructor(imageVector: ImageVector): this(ImageVectorWrapper(imageVector), null)

        override val exportString: String
            get() = Json.encodeToString(kotlinx.serialization.serializer(), this)

        val imageVector: ImageVector
            get() = imageVectorWrapper.imageVector

        override fun toString(): String {
            return "Resource.Vector(name=${imageVector.name})"
        }
    }

    @Immutable
    @Serializable
    data class Color(private val colorWrapper: ColorWrapper) : Resource {

        constructor(color: wallapp.graphics.Color): this(ColorWrapper(color.value))

        val color: wallapp.graphics.Color by lazy {
            Color(colorWrapper.color)
        }

        override val contentDescription: String? = null

        override val exportString: String
            get() = Json.encodeToString(kotlinx.serialization.serializer(), this)
    }

    @Immutable
    @Serializable
    data class HashedImage(
        val imageHash: ImageHash,
        override val contentDescription: String? = null,
    ): Resource {
        override val exportString: String
            get() = Json.encodeToString(kotlinx.serialization.serializer(), this)
    }

    @Immutable
    @Serializable
    data object Placeholder : Resource {
        val value: String = ""

        override val contentDescription: String? = null

        override val exportString: String
            get() = Json.encodeToString(kotlinx.serialization.serializer(), this)
    }

    @Immutable
    @Serializable
    data class AnimatedImage(
        val animatedImageSpec: AnimatedImageSpec,
        /**
         * If specified, [fallback] will be used instead of [animatedImageSpec] when rendering via
         * Compose on iOS. This is to work around the limitation being unable to use transparency
         * when rendering Lottie animations via Compose on iOS.
         */
        val fallback: Resource?,
    ): Resource {
        override val contentDescription: String? = null
        override val exportString: String
            get() = Json.encodeToString(kotlinx.serialization.serializer(), this)
    }

    @Immutable
    @Serializable
    data class RawByteArray(val byteArray: ByteArray): Resource {
        override val contentDescription: String? = null
        override val exportString: String
            get() = Json.encodeToString(kotlinx.serialization.serializer(), this)

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as RawByteArray
            if (!byteArray.contentEquals(other.byteArray)) return false
            return contentDescription == other.contentDescription
        }

        override fun hashCode(): Int {
            var result = byteArray.contentHashCode()
            result = 31 * result + (contentDescription?.hashCode() ?: 0)
            return result
        }
    }

    @Immutable
    @Serializable
    data class AnimatedAssetFile(val fileName: String): Resource {
        override val contentDescription: String? = null
        override val exportString: String
            get() = Json.encodeToString(kotlinx.serialization.serializer(), this)
    }


    @Immutable
//    @Serializable
    data class LocalImageAssetFile(
        val fileName: String,
        val drawableResource: DrawableResource,
    ): Resource {
        override val contentDescription: String? = null
        override val exportString: String
            get() = Json.encodeToString(kotlinx.serialization.serializer(), this)
    }

    @Immutable
    @Serializable
    data class LocalFileResource(
        val fileName: String,
    ): Resource {
        override val contentDescription: String? = null
        override val exportString: String
            get() = Json.encodeToString(kotlinx.serialization.serializer(), this)
    }

    actual companion object {
        val Json = Json {
            useArrayPolymorphism = true
        }

        actual fun fromExportString(exportString: String): Resource {
            try {
                return Json.decodeFromString(kotlinx.serialization.serializer(), exportString)
            } catch (ex: SerializationException) {
                Log.e("Resource", "Failed to parse Resource from exportString: $exportString", ex)
                throw ex
            }
        }

        actual fun from(model: Any): Resource {
            return when (model) {
                is ImageVector -> {
                    Vector(model)
                }

                is wallapp.graphics.Color -> {
                    Color(model)
                }

                is String -> {
                    when {
                        model.startsWith("http") -> {
                            return UrlImage(model)
                        }
                        else -> {
                            throw IllegalArgumentException("Unsupported model: $model")
                        }
                    }
                }

                is LocalAsset -> {
                    model.toResource()
                }

                is LocalFileResource -> {
                    model
                }

                else -> {
                    throw IllegalArgumentException("Unsupported model: $model")
                }
            }
        }

        actual val Preset: Resource = UrlImage("https://example.com")
    }
}
