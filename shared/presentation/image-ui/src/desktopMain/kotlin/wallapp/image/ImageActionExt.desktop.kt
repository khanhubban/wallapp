package wallapp.image

import com.seiko.imageloader.model.ImageAction as SeikoImageAction

actual fun mapSeikoImageAction(imageUrl: String, action: SeikoImageAction): ImageAction =
    when (action) {
        is SeikoImageAction.Failure -> {
            when (val error = action.error) {
                is java.io.IOException -> ImageAction.Error.ImageDecodeError(imageUrl, error)
                else -> ImageAction.Error.GeneralError(imageUrl, error)
            }
        }
        is SeikoImageAction.Success -> ImageAction.Success
        is SeikoImageAction.Loading -> ImageAction.Loading
    }
