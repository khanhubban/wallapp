package wallapp.network

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import wallapp.log.Log

interface NetworkErrorBroadcaster {
    val networkErrorOccurred: Flow<Unit>
    fun reportNetworkError(source: String? = null)
}

object NetworkErrorBroadcasterNoOp : NetworkErrorBroadcaster {
    override val networkErrorOccurred: Flow<Unit>
        get() = MutableSharedFlow<Unit>().asSharedFlow()

    override fun reportNetworkError(source: String?) {
        Log.e("[NRW] Network error occurred, source: $source")
    }
}

class NetworkErrorBroadcasterDefault : NetworkErrorBroadcaster {
    private val _networkErrorOccurred = Channel<Unit>(BUFFERED)
    override val networkErrorOccurred: Flow<Unit>
        get() = _networkErrorOccurred.receiveAsFlow()

    override fun reportNetworkError(source: String?) {
        Log.e("[NRW] Network error occurred, source: $source")
        _networkErrorOccurred.trySendBlocking(Unit)
    }
}