package wallapp.network

import kotlinx.coroutines.flow.MutableStateFlow

class NetworkStatePreset(
    networkState: NetworkConnectionState = NetworkConnectionState.Connected,
) : NetworkState {

    override val networkConnectionState = MutableStateFlow(networkState)

    fun setState(state: NetworkConnectionState) {
        networkConnectionState.value = state
    }

    override val isConnected: Boolean
        get() = networkConnectionState.value == NetworkConnectionState.Connected
}