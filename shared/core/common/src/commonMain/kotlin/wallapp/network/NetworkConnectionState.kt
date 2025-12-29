package wallapp.network

import co.touchlab.skie.configuration.annotations.EnumInterop

@EnumInterop.Enabled
enum class NetworkConnectionState(val state: Int) {
    Unknown(-1),
    Disconnected(0),
    Connected(1),
    ConnectionNoInternet(2), // This seems strange, but is possible on Android. See #2146.
    ;

    fun asNetworkState(): String {
        return when (this) {
            Unknown -> "Unknown"
            Disconnected -> "Disconnected"
            Connected -> "Connected"
            ConnectionNoInternet -> "ConnectionNoInternet"
        }
    }
}

val NetworkConnectionState.isConnected: Boolean
    get() = this == NetworkConnectionState.Connected
