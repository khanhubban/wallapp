package wallapp.network

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NetworkUserManagerAdmin : NetworkUserManager {
    override val isReady: StateFlow<Boolean> = MutableStateFlow(true)

    override suspend fun waitUntilReady() {
        return
    }
}