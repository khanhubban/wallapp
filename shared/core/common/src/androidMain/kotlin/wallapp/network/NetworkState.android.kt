package wallapp.network

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import wallapp.appvisibility.AppVisibility
import wallapp.coroutine.CoroutineScopes
import wallapp.coroutine.collectIn
import wallapp.log.Logger
import wallapp.util.combine
import kotlin.time.Duration.Companion.seconds

class NetworkStateAndroid(
    private val connectivityManager: ConnectivityManager,
    private val appVisibility: AppVisibility,
    private val coroutineScopes: CoroutineScopes,
) : ConnectivityManager.NetworkCallback(), NetworkState {

    companion object {
        private val Log = Logger("[NetworkStateAndroid]")
        private val PollingInterval = .5.seconds
    }

    override val networkConnectionState: MutableStateFlow<NetworkConnectionState> =
        MutableStateFlow(connectivityManager.getActiveNetworkConnectionState())

    private var pollingJob: Job? = null

    private val coroutineScopeIo: CoroutineScope
        get() = coroutineScopes.io
    private val appIsVisible: Flow<Boolean>
        get() = appVisibility.isVisible
    private val currentNetwork: MutableStateFlow<Network?> = MutableStateFlow(null)

    override fun onAvailable(network: Network) {
        currentNetwork.value = network
    }

    override fun onLost(network: Network) {
        currentNetwork.value = null
    }

    fun setState(state: NetworkConnectionState) {
        if (networkConnectionState.value == state) return
        networkConnectionState.value = state
        Log.i("networkConnectionState to \"%s\"", state.asNetworkState())
    }

    override val isConnected: Boolean
        get() = networkConnectionState.value.isConnected

    private fun getConnectivityAsState(): NetworkConnectionState {
        val network = currentNetwork.value
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return if (networkCapabilities != null
            && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
            // Require validated network before classifying as connected. See #2145.
            if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
//                Log.v("Network available and validated for internet connection.")
                NetworkConnectionState.Connected
            } else {
                Log.v("Network available, but not validated for internet connection.")
                NetworkConnectionState.ConnectionNoInternet
            }
        } else {
            Log.v("Network available, but no internet capability.")
            NetworkConnectionState.Disconnected
        }
    }

    private fun startPolling() {
        pollingJob?.cancel()  // Cancel any existing job to avoid duplicate polling
        pollingJob = coroutineScopeIo.launch {
            while (coroutineContext.isActive) {
                setState(getConnectivityAsState())
                delay(PollingInterval)
            }
        }
    }

    private fun stopPolling() {
        pollingJob?.cancel()
    }

    private val doPolling: Flow<Boolean> = combine(appIsVisible, currentNetwork) { isVisible, _ ->
        isVisible
    }

    init {
        connectivityManager.registerDefaultNetworkCallback(this)

        doPolling.collectIn(coroutineScopeIo) { isVisible ->
            if (isVisible) {
                startPolling()
            } else {
                stopPolling()
            }
        }
    }
}
