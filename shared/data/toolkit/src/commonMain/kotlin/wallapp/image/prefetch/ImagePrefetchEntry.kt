package wallapp.image.prefetch

import androidx.compose.runtime.Immutable
import wallapp.image.cache.ImageCacheSpec
import wallapp.image.imageUrl
import wallapp.pixel.image.ImageViewState
import wallapp.string.quote

@Immutable
data class ImagePrefetchEntry private constructor(
    val id: String?,
    val imageViewState: ImageViewState,
    val imageCacheSpec: ImageCacheSpec,
) {
    val debugStringShort: String
        get() = "ImagePrefetchEntry(viewId=${id?.quote()}, imageCacheSpec=$imageCacheSpec)"

    companion object {
        fun from(
            id: String?,
            imageViewState: ImageViewState,
            imageCacheSpec: ImageCacheSpec,
        ): ImagePrefetchEntry? {
            if (imageViewState.image.imageUrl == null) {
                return null
            }

            return ImagePrefetchEntry(
                id = id,
                imageViewState = imageViewState,
                imageCacheSpec = imageCacheSpec,
            )
        }
    }
}
