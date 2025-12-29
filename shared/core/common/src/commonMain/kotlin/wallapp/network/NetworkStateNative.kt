package wallapp.network

interface NetworkStateNative {

    fun registerNetworkStateListener(listener: (NetworkConnectionState) -> Unit)
}