package wallapp.resource

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.DrawableResource
import wallapp.graphics.Color
import wallapp.log.Log

@Serializable
actual sealed interface Resource {

    actual val contentDescription: String?

    actual val exportString: String

    @Immutable
    @Serializable
    abstract class ResourceImpl: Resource {
        override val contentDescription: String?
            get() = null

        override val exportString: String
            get() = Json.encodeToString(kotlinx.serialization.serializer(), this)
    }

    @Immutable
    @Serializable
    data class UrlImage(val url: String): ResourceImpl()

    @Immutable
    @Serializable
    data class Vector(
        private val imageVectorWrapper: ImageVectorWrapper,
        override val contentDescription: String?,
    ): ResourceImpl() {

        constructor(imageVector: ImageVector): this(ImageVectorWrapper(imageVector), null)

        val imageVector: ImageVector
            get() = imageVectorWrapper.imageVector

        override fun toString(): String {
            return "Resource.Vector(name=${imageVector.name})"
        }
    }

    @Immutable
    @Serializable
    data class Color(private val colorWrapper: ColorWrapper) : ResourceImpl() {

        constructor(color: wallapp.graphics.Color): this(ColorWrapper(color.value))

        val color: wallapp.graphics.Color by lazy {
            Color(colorWrapper.color)
        }
    }

    @Immutable
    data class DrawableResourceCompose(val drawable: DrawableResource): ResourceImpl()

    @Immutable
    @Serializable
    data class FileResourceCompose(
        val fileName: String,
    ): ResourceImpl()

    @Immutable
    @Serializable
    data object Placeholder : ResourceImpl() {
        val value: String = ""
    }

    @Immutable
    @Serializable
    data class HashedImage(
        val imageHash: ImageHash,
    ): ResourceImpl()

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

                is FileResourceCompose -> {
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
