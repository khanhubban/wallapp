package wallapp.image

import co.touchlab.skie.configuration.annotations.SealedInterop

@SealedInterop.Enabled
sealed class ImageModel {

    abstract val url: String
    abstract val contentDescription: String?
    abstract val loadingModel: Any?

    data class UrlImage(
        override val url: String,
        override val contentDescription: String? = null,
        override val loadingModel: Any? = null,
    ) : ImageModel()

    data class UrlVideo(
        override val url: String,
        override val contentDescription: String? = null,
        override val loadingModel: Any? = null,
    ) : ImageModel()

    companion object {

        fun from(
            model: Any,
            contentDescription: String? = null,
            loadingModel: Any? = null,
        ): ImageModel {
            return when (model) {
                is String -> {
                    require(model.startsWith("http")) {
                        "Unsupported model: $model - only \"http\" is supported"
                    }

                    if (isVideoUrl(model)) {
                        UrlVideo(model, contentDescription, loadingModel)
                    } else {
                        UrlImage(model, contentDescription, loadingModel)
                    }
                }

                is UrlImage -> {
                    model.copy(contentDescription = contentDescription, loadingModel = loadingModel)
                }

                is UrlVideo -> {
                    model.copy(contentDescription = contentDescription, loadingModel = loadingModel)
                }

                else -> throw IllegalArgumentException("Unknown model type: $model")
            }
        }

        fun isVideoUrl(url: String): Boolean {
            val lower = url.lowercase()
            return lower.endsWith(".mp4")
        }

        val Preset = from("https://example.com/image.jpg")
    }

}