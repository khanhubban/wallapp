package wallapp.network

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface NetworkUserManager {

    val isReady: StateFlow<Boolean>

    suspend fun waitUntilReady()
}

class NetworkUserManagerNoOp : NetworkUserManager {
    override val isReady: StateFlow<Boolean> = MutableStateFlow(false)

    override suspend fun waitUntilReady() {
        return
    }
}
