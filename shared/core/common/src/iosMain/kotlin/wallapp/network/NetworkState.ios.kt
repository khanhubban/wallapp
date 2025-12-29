package wallapp.network

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NetworkStateIos(
    networkStateNative: NetworkStateNative
) : NetworkState {

    private val _networkConnectionState = MutableStateFlow(NetworkConnectionState.Unknown)
    override val networkConnectionState: StateFlow<NetworkConnectionState>
        get() = _networkConnectionState

    override val isConnected: Boolean
        get() = _networkConnectionState.value == NetworkConnectionState.Connected

    init {
        networkStateNative.registerNetworkStateListener {
            _networkConnectionState.value = it
        }
    }
}