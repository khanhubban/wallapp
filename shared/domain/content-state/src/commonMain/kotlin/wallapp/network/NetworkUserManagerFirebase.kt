package wallapp.network

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import wallapp.auth.firebase.FirebaseAuthManager

class NetworkUserManagerFirebase(
    private val firebaseAuthManager: FirebaseAuthManager,
    coroutineScopeIo: CoroutineScope,
) : NetworkUserManager {

    override val isReady: StateFlow<Boolean> =
        firebaseAuthManager.firebaseAuthUser
            .map { it != null }
            .stateIn(coroutineScopeIo, started = SharingStarted.Eagerly, initialValue = false)

    override suspend fun waitUntilReady() {
        firebaseAuthManager.firebaseAuthUser.first { it != null }
    }
}