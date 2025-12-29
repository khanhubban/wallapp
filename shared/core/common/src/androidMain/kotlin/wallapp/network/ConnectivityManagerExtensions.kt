package wallapp.network

import android.net.ConnectivityManager
import android.net.NetworkCapabilities

fun ConnectivityManager.getActiveNetworkConnectionState(): NetworkConnectionState {
    return try {
        getNetworkCapabilities(activeNetwork)?.let {
            when {
                (it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                        || it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) -> {
                    NetworkConnectionState.Connected
                }
                else -> NetworkConnectionState.Disconnected
            }
        } ?: NetworkConnectionState.Disconnected
    } catch (e: SecurityException) {
        /**
         * Huawei 8.0 devices can raise a SecurityException from a
         * [android.net.NetworkInfo.isConnected()] call. See #1400.
         */
        NetworkConnectionState.Unknown
    }
}