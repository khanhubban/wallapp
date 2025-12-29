package wallapp.mediamap

import kotlinx.coroutines.flow.StateFlow
import wallapp.image.sized.SizedImage
import wallapp.initialization.InitializationState
import wallapp.media.model.MediaHolder

interface MediaMapRepository {

    val initializationState: StateFlow<InitializationState>
    val isReady: StateFlow<Boolean>

    fun getMedia(
        mediaHolder: MediaHolder,
        sizedImage: SizedImage,
    ): MediaMapGetResult

}