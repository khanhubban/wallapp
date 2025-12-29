package wallapp.network

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NetworkUserManagerPreset : NetworkUserManager {
    override val isReady: StateFlow<Boolean>
        get() = MutableStateFlow(true)

    override suspend fun waitUntilReady() { }
}