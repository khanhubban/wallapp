package wallapp.network

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transformLatest
import wallapp.app.AppStateManager
import wallapp.content.state.error.ErrorScreen
import wallapp.coroutine.collectIn
import wallapp.data.model.ModelRepositoryRaw
import wallapp.lifecycle.AppLifecycleManager
import wallapp.log.Log
import wallapp.mediamap.MediaMapRepositoryDefault
import wallapp.navigation.AppNavigator
import wallapp.remoteapi.RemoteApiEncryptionConfig
import wallapp.remoteendpoint.RemoteApiEndpointRepositoryDefault
import wallapp.search.content.SearchContentRepositoryDefault
import wallapp.time.TimeRepository
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

interface NetworkRefreshManager {
    val allDataLoaded: StateFlow<Boolean>

    val searchDataLoaded: StateFlow<Boolean>

    fun initialize()

    fun requestUserRefresh()
}

object NetworkRefreshManagerNoOp : NetworkRefreshManager {
    override val allDataLoaded: StateFlow<Boolean> = MutableStateFlow(true)

    override val searchDataLoaded: StateFlow<Boolean> = MutableStateFlow(true)

    override fun initialize() {
        // no-op
    }

    override fun requestUserRefresh() {
        // no-op
    }
}

@Suppress("OPT_IN_USAGE")
class NetworkRefreshManagerDefault(
    private val timeRepository: TimeRepository,
    private val networkState: NetworkState,
    private val appLifecycleManager: AppLifecycleManager,
    private val networkRefreshTriggerBroadcaster: NetworkRefreshTriggerBroadcaster,
    private val networkUserManager: NetworkUserManager,
    private val remoteApiEncryptionConfig: RemoteApiEncryptionConfig,
    private val remoteApiEndpointRepository: RemoteApiEndpointRepositoryDefault,
    private val modelRepositoryRaw: ModelRepositoryRaw,
    private val mediaMapRepositoryDefault: MediaMapRepositoryDefault,
    private val searchContentRepositoryDefault: SearchContentRepositoryDefault,
    private val networkErrorBroadcaster: NetworkErrorBroadcaster,
    private val appNavigator: AppNavigator,
    private val appStateManager: AppStateManager,
    private val coroutineScopeIo: CoroutineScope,
    private val coroutineScopeMain: CoroutineScope,
) : NetworkRefreshManager {

    private val requestUserRefreshChannel = Channel<Long>(Channel.BUFFERED)
    private val requestUserRefresh = requestUserRefreshChannel.receiveAsFlow()

    private var lastRefreshRequestTime = -1L
    private val requestRefreshChannel = Channel<Long>(Channel.BUFFERED)
    private val requestRefresh = requestRefreshChannel.receiveAsFlow()
        .filter {
            val currentTime = timeRepository.currentTime
            val shouldRefresh = currentTime - lastRefreshRequestTime >= RequestRefreshThresholdDuration.inWholeMilliseconds
            lastRefreshRequestTime = currentTime
            shouldRefresh
        }

    private var lastTriggerTime: Long = timeRepository.currentTime
    private val automaticRefreshTrigger = flow {
        while (true) {
            if (shouldRefresh()) {
                emit(timeRepository.currentTime)
                lastTriggerTime = timeRepository.currentTime
            }
            delay(CheckForRefreshDuration.inWholeMilliseconds)
        }
    }

    private fun shouldRefresh(): Boolean {
        val elapsedTime = timeRepository.currentTime - lastTriggerTime
        return elapsedTime >= RefreshIntervalDuration.inWholeMilliseconds
    }

    private val anyCacheableDataNotAvailable: Boolean
        get() = remoteApiEndpointRepository.remoteEndpointsFromCache.value == null
                || modelRepositoryRaw.networkContentFromCache.value == null
                || mediaMapRepositoryDefault.mediaMapFromCache.value == null

    private val anyDataNotAvailable: Boolean
        get() = (remoteApiEncryptionConfig.key.value.isBlank()
                || remoteApiEndpointRepository.remoteEndpointsFromNetwork.value == null
                || modelRepositoryRaw.networkContentFromNetwork.value == null)
            .also {
                Log.d("[NRW] anyDataNotAvailable: $it")
            }
    // This is not needed as search content is not mandatory for app to load
//                || searchContentRepositoryDefault.fetchNetworkContentNullable.value == null

    private var isInitialized = false
    override fun initialize() {
        if (isInitialized) {
            return
        }
        isInitialized = true
        appLifecycleManager.isAppInForeground.collectIn(coroutineScopeIo) { appInForeground ->
            if (appInForeground) {
                requestRefreshChannel.trySendBlocking(timeRepository.currentTime)
            }
        }

        // This code refreshes all the network data after [RefreshIntervalDuration] if app is alive and
        // as soon as network is available and user is signed in
        automaticRefreshTrigger.transformLatest {
            Log.d("[NRW-F] nRR, automaticRefreshTrigger, connection: ${networkState.networkConnectionState.value}")
            // wait for connection
            networkState.networkConnectionState.first { it.isConnected }
            if (!networkUserManager.isReady.value) {
                networkRefreshTriggerBroadcaster.requestRefreshFor(NetworkDataType.UserSignIn)
            }
            networkUserManager.waitUntilReady()
            if (remoteApiEncryptionConfig.key.value.isBlank()) {
                networkRefreshTriggerBroadcaster.requestRefreshFor(NetworkDataType.Key)
            }
            remoteApiEncryptionConfig.key.first { it.isNotBlank() }
            networkRefreshTriggerBroadcaster.requestRefreshFor(NetworkDataType.RemoteEndpoints)
            networkRefreshTriggerBroadcaster.requestRefreshFor(NetworkDataType.NetworkContent)
            networkRefreshTriggerBroadcaster.requestRefreshFor(NetworkDataType.MediaMap)
            networkRefreshTriggerBroadcaster.requestRefreshFor(NetworkDataType.SearchContent)
            emit(Unit)
        }.launchIn(coroutineScopeIo)

        // This code only refreshes the network data that is not available if the network is available
        // and the user is signed in
        combine(
            networkState.networkConnectionState,
            requestRefresh,
            networkUserManager.isReady,
            remoteApiEncryptionConfig.key,
            requestUserRefresh,
        ) { networkConnectionState, _, userIsReady, key, _ ->
            Log.d("[NRW-F] nRM, connection: $networkConnectionState, userIsReady: $userIsReady")
            if (!networkConnectionState.isConnected && anyCacheableDataNotAvailable) {
                networkErrorBroadcaster.reportNetworkError("nRR, networkConnectionState")
                return@combine
            }

            if (!userIsReady) {
                networkRefreshTriggerBroadcaster.requestRefreshFor(NetworkDataType.UserSignIn)
                return@combine
            }

            if (key.isBlank()) {
                networkRefreshTriggerBroadcaster.requestRefreshFor(NetworkDataType.Key)
                return@combine
            }

            if (remoteApiEndpointRepository.remoteEndpointsFromNetwork.value == null) {
                networkRefreshTriggerBroadcaster.requestRefreshFor(NetworkDataType.RemoteEndpoints)
            }

            if (modelRepositoryRaw.networkContentFromNetwork.value == null) {
                networkRefreshTriggerBroadcaster.requestRefreshFor(NetworkDataType.NetworkContent)
            }
            if (mediaMapRepositoryDefault.mediaMapFromNetwork.value == null) {
                networkRefreshTriggerBroadcaster.requestRefreshFor(NetworkDataType.MediaMap)
            }
            // Search content is not critical so only refresh if it's not in cache either
            if (searchContentRepositoryDefault.searchContentFromCache.value == null) {
                networkRefreshTriggerBroadcaster.requestRefreshFor(NetworkDataType.SearchContent)
            }
        }.launchIn(coroutineScopeIo)

        // All network errors including network state not connected are handled here
        networkErrorBroadcaster.networkErrorOccurred
            .debounce(2000)
            .transformLatest { networkErrorOccurred ->
                val dataNotReady = anyDataNotAvailable && anyCacheableDataNotAvailable
                Log.d("[NRW] networkErrorOccurred: $networkErrorOccurred, dataNotReady: $dataNotReady")
                // wait for a screen to be shown before showing the error
                appNavigator.currentScreenFlow.first { it != null }
                if (dataNotReady) {
                    appStateManager.navigateToError(ErrorScreen.RemoteDataFetch)
                }
                emit(networkErrorOccurred)
            }.launchIn(coroutineScopeMain)
    }

    override fun requestUserRefresh() {
        requestUserRefreshChannel.trySendBlocking(timeRepository.currentTime)
    }

    override val allDataLoaded: StateFlow<Boolean> = combine(
        remoteApiEncryptionConfig.key,
        remoteApiEndpointRepository.remoteEndpointsFromCache,
        modelRepositoryRaw.networkContentFromCache,
        mediaMapRepositoryDefault.mediaMapFromCache,
        searchContentRepositoryDefault.searchContentFromCache,
    ) { key, remoteEndpoints, networkContent, mediaMap, searchContent ->
        Log.d("[NRW] allDataLoaded: ${key.isNotBlank()}, ${remoteEndpoints != null}, ${networkContent != null}, ${mediaMap != null}, ${searchContent != null}")
        key.isNotBlank() && remoteEndpoints != null && networkContent != null && mediaMap != null && searchContent != null
    }.stateIn(
        scope = coroutineScopeIo,
        started = SharingStarted.Eagerly,
        initialValue = false
    )

    override val searchDataLoaded: StateFlow<Boolean> = searchContentRepositoryDefault.searchContentFromCache
        .map { it != null }
        .stateIn(
            scope = coroutineScopeIo,
            started = SharingStarted.Eagerly,
            initialValue = false
        )

    init {
        // Send first emission for combine call to start
        requestUserRefreshChannel.trySendBlocking(timeRepository.currentTime)
    }

    companion object {
        private val RefreshIntervalDuration: Duration = 6.hours
        private val CheckForRefreshDuration: Duration = 1.hours
        private val RequestRefreshThresholdDuration: Duration = 10.seconds
    }
}