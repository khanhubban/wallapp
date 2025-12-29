package wallapp.image.host

import kotlinx.coroutines.flow.MutableStateFlow
import wallapp.appstate.AppState

class ImageHostManagerConfigDefault(
    private val appState: AppState,
) : ImageHostManagerConfig {

    override val imageFormatCodeInAppCompose: MutableStateFlow<String>
        get() = appState.imageFormatCodeInAppCompose
}