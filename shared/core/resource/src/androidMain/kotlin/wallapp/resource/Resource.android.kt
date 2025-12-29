package wallapp.resource

import androidx.annotation.DrawableRes
import androidx.annotation.FontRes
import androidx.annotation.RawRes
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.DrawableResource
import wallapp.log.Log
import android.graphics.Bitmap as BitmapSystem
import android.graphics.drawable.Drawable as DrawableSystem
import wallapp.graphics.Color as ActionColor

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
    data class Raw(
        @RawRes val rawRes: Int,
    ): ResourceImpl()

    @Immutable
    @Serializable
    data class Drawable(@DrawableRes val drawableRes: Int): ResourceImpl()

//    @Serializable
    @Immutable
    data class DrawableAndroid(val drawable: DrawableSystem?): ResourceImpl()

    @Immutable
//    @Serializable
    data class DrawableCompose(val drawable: DrawableResource): ResourceImpl()

    @Immutable
    @Serializable
    data class Font(@FontRes val fontRes: Int): ResourceImpl()

    @Immutable
    @Serializable
    data class UrlImage(val url: String): ResourceImpl()

    @Immutable
    @Serializable
    data class UrlVideo(val url: String): ResourceImpl()

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

        constructor(color: ActionColor): this(ColorWrapper(color.value))

        val color: ActionColor by lazy {
            ActionColor(colorWrapper.color)
        }
    }

    @Immutable
    @Serializable
    data class FileUri(val fileUri: String): ResourceImpl()

    @Immutable
    @Serializable
    data class AnimatedImage(val spec: AnimatedImageSpec): ResourceImpl()

    @Immutable
    @Serializable
    data class HashedImage(
        val imageHash: ImageHash,
    ): ResourceImpl()

    @Immutable
//    @Serializable
    data class SystemBitmap(
        val bitmap: BitmapSystem,
    ): ResourceImpl()

    @Immutable
    @Serializable
    data class FileResourceCompose(
        val fileName: String,
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
                    return when {
                        model.startsWith("http") -> {
                            UrlImage(model)
                        }
                        else -> {
                            HashedImage(ImageHash.from(model))
                        }
                    }
                }

                is DrawableSystem -> {
                    DrawableAndroid(model)
                }

                is BitmapSystem -> {
                    SystemBitmap(model)
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

        actual val Preset: Resource = Drawable(-1)
    }

}

