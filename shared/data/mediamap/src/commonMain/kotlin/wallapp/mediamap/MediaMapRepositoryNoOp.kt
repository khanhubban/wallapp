package wallapp.mediamap

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import wallapp.image.sized.SizedImage
import wallapp.initialization.InitializationState
import wallapp.media.model.MediaHolder

class MediaMapRepositoryNoOp : MediaMapRepository {
    override val initializationState: StateFlow<InitializationState> =
        MutableStateFlow(InitializationState.Ready)

    override val isReady: StateFlow<Boolean> = MutableStateFlow(true)

    override fun getMedia(mediaHolder: MediaHolder, sizedImage: SizedImage): MediaMapGetResult {
        return MediaMapGetResult.NotFound.NotFoundMediaId(mediaHolder.mediaId, sourceId = null)
    }
}