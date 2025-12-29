package wallapp.mediamap

import wallapp.image.ImageModel
import wallapp.image.ImageSize
import wallapp.image.sized.SizedImage
import wallapp.media.model.MediaId

sealed class MediaMapGetResult {

    data class Success(
        val imageModel: ImageModel,
        val imageSize: ImageSize?,
    ) : MediaMapGetResult()

    sealed class NotFound: MediaMapGetResult() {

        data class NotFoundMediaId(
            val mediaId: MediaId,
            val sourceId: String?,
        ): NotFound()

        data class NotFoundSizedImage(
            val mediaId: MediaId,
            val missingSizedImage: SizedImage,
            val validSizedImages: List<SizedImage>,
            val sourceId: String?,
        ): NotFound()
    }
}