package wallapp.data.content.media

import wallapp.image.ImageModel
import wallapp.media.model.MediaId

sealed class ContentMediaGetResult {

    data class Success(
        val imageModel: ImageModel,
        val mediaId: MediaId,
    ) : ContentMediaGetResult()

    data class Error(
        val errorMessage: String,
    ) : ContentMediaGetResult()

}