package wallapp.network

import co.touchlab.skie.configuration.annotations.FlowInterop
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import wallapp.log.Log
import wallapp.time.TimeRepository

interface NetworkRefreshTriggerBroadcaster {

    @FlowInterop.Enabled
    val userSignInRefresh: Flow<Long>

    val keyRefresh: Flow<Long>

    val remoteEndpointsRefresh: Flow<Long>

    val networkContentRefresh: Flow<Long>

    val mediaMapRefresh: Flow<Long>

    val searchContentRefresh: Flow<Long>

    fun requestRefreshFor(networkDataType: NetworkDataType)
}

object NetworkRefreshTriggerBroadcasterNoOp : NetworkRefreshTriggerBroadcaster {

    override val userSignInRefresh: Flow<Long> = emptyFlow()
    override val keyRefresh: Flow<Long> = emptyFlow()
    override val remoteEndpointsRefresh: Flow<Long> = emptyFlow()
    override val networkContentRefresh: Flow<Long> = emptyFlow()
    override val mediaMapRefresh: Flow<Long> = emptyFlow()
    override val searchContentRefresh: Flow<Long> = emptyFlow()

    override fun requestRefreshFor(networkDataType: NetworkDataType) {}
}

class NetworkRefreshTriggerBroadcasterDefault(
    private val timeRepository: TimeRepository,
    private val coroutineScopeIo: CoroutineScope,
) : NetworkRefreshTriggerBroadcaster {

    private val refreshMutex = Mutex()

    private val _userSignInRefresh = MutableSharedFlow<Long>(replay = 1)
    override val userSignInRefresh: Flow<Long>
        get() = _userSignInRefresh.asSharedFlow()

    private val _keyRefresh = MutableSharedFlow<Long>(replay = 1)
    override val keyRefresh: Flow<Long>
        get() = _keyRefresh.asSharedFlow()

    private val _remoteEndpointsRefresh = MutableSharedFlow<Long>(replay = 1)
    override val remoteEndpointsRefresh: Flow<Long>
        get() = _remoteEndpointsRefresh.asSharedFlow()

    private val _networkContentRefresh = MutableSharedFlow<Long>(replay = 1)
    override val networkContentRefresh: Flow<Long>
        get() = _networkContentRefresh.asSharedFlow()

    private val _mediaMapRefresh = MutableSharedFlow<Long>(replay = 1)
    override val mediaMapRefresh: Flow<Long>
        get() = _mediaMapRefresh.asSharedFlow()

    private val _searchContentRefresh = MutableSharedFlow<Long>(replay = 1)
    override val searchContentRefresh: Flow<Long>
        get() = _searchContentRefresh.asSharedFlow()
    

    override fun requestRefreshFor(networkDataType: NetworkDataType) {
        coroutineScopeIo.launch {
            refreshMutex.withLock {
                Log.d("[NRW-F] nRM, requestRefreshFor: $networkDataType")
                val currentTime = timeRepository.currentTime
                when (networkDataType) {
                    NetworkDataType.UserSignIn -> {
                        _userSignInRefresh.emit(currentTime)
                    }
                    NetworkDataType.Key -> {
                        _keyRefresh.emit(currentTime)
                    }
                    NetworkDataType.RemoteEndpoints -> {
                        _remoteEndpointsRefresh.emit(currentTime)
                    }
                    NetworkDataType.NetworkContent -> {
                        _networkContentRefresh.emit(currentTime)
                    }
                    NetworkDataType.MediaMap -> {
                        _mediaMapRefresh.emit(currentTime)
                    }
                    NetworkDataType.SearchContent -> {
                        _searchContentRefresh.emit(currentTime)
                    }
                }
            }
        }
    }
}