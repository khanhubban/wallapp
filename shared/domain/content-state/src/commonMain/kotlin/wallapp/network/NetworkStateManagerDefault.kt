package wallapp.network

import kotlinx.coroutines.CoroutineScope
import wallapp.app.AppStateManager
import wallapp.content.state.error.ErrorScreen
import wallapp.coroutine.collectIn

class NetworkStateManagerDefault(
    networkState: NetworkState,
    private val appStateManager: AppStateManager,
    coroutineScopeMain: CoroutineScope,
) : NetworkStateManager {

    private fun updateNetworkConnectionState(state: NetworkConnectionState) {
        if (state == NetworkConnectionState.Disconnected || state == NetworkConnectionState.ConnectionNoInternet) {
            appStateManager.navigateToError(ErrorScreen.Network())
        }
    }

    init {
        networkState.networkConnectionState.collectIn(coroutineScopeMain) {
            updateNetworkConnectionState(state = it)
        }
    }

}