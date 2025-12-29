package wallapp.image

import androidx.compose.runtime.Immutable
import co.touchlab.skie.configuration.annotations.SealedInterop
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import wallapp.graphics.Color
import wallapp.resource.Resource
import wallapp.theme.ColorToken
import kotlin.experimental.ExperimentalObjCName
import kotlin.native.ObjCName

@OptIn(ExperimentalObjCName::class)
@SealedInterop.Enabled
@Serializable
@ObjCName("ImageUI")
sealed interface Image {

    val id: String
    val contentDescription: String?
    val tintColorToken: ColorToken?
        get() = null
    val imageSize: ImageSize?
        get() = null

    @Immutable
    @Serializable
    data class ImageResource internal constructor(
        val resource: Resource,
        override val contentDescription: String?,
        override val imageSize: ImageSize?,
        override val tintColorToken: ColorToken? = null,
    ) : Image {

        override val id: String
            get() = resource.toString()

        companion object {
            val Preset = ImageResource(
                resource = Resource.Preset,
                contentDescription = null,
                imageSize = null,
            )
        }
    }

    @Immutable
    @Serializable
    data class ImageStates internal constructor(
        val success: Image,
        val loading: Image?,
//        val error: Image?,
    ) : Image {
        override val id: String
            get() = "ImageStates(${success.id}"

        override val contentDescription: String?
            get() = success.contentDescription

        override val imageSize: ImageSize?
            get() = success.imageSize

        override val tintColorToken: ColorToken?
            get() = success.tintColorToken
    }

    val exportString: String
        get() = Json.encodeToString(kotlinx.serialization.serializer(), this)

    companion object {
        val Preset = ImageResource.Preset
        val PresetColorBlue by lazy { from(Color.Blue) }
        val PresetColor by lazy { PresetColorBlue }
        val PresetUrl by lazy {
            ImageModel.from("https://is2-ssl.mzstatic.com/image/thumb/Podcasts126/v4/07/af/f5/07aff50e-6a28-6f3f-7db3-99335ab82b8a/mza_7024223367047886200.jpeg/626x0w.webp")
        }

        fun from(
            model: Any,
            contentDescription: String? = null,
            imageOptions: ImageOptions? = null,
            loadingModel: Any? = null,
        ): Image {
            val successImage = imageFrom(model, contentDescription, imageOptions)
            return if (loadingModel != null) {
                val loadingImage = imageFrom(loadingModel, contentDescription)
                ImageStates(
                    success = successImage,
                    loading = loadingImage,
                )
            } else {
                successImage
            }
        }

        fun from(
            model: Resource,
            contentDescription: String? = model.contentDescription,
            imageOptions: ImageOptions? = null,
        ): Image {
            return imageFrom(model, contentDescription, imageOptions)
        }

        fun fromExportString(exportString: String): Image {
            return Json.decodeFromString(kotlinx.serialization.serializer(), exportString)
        }
    }
}

expect fun imageFrom(
    model: Any,
    contentDescription: String? = null,
    imageOptions: ImageOptions? = null,
): Image

expect fun Image.updateAnimatedSpecWith(
    startAnimation: Boolean,
    animationStarted: () -> Unit = {},
    animationCompleted: () -> Unit = {},
    animationProgressUpdates: (Float) -> Unit = {},
): Image