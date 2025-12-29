package wallapp.network

import kotlinx.coroutines.flow.StateFlow

interface NetworkState {
    val networkConnectionState: StateFlow<NetworkConnectionState>

    val isConnected: Boolean
}
