package wallapp.remoteapi

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import wallapp.data.DataRepository
import wallapp.download.DownloadState
import wallapp.download.FirebaseStorageDownloader
import wallapp.log.Log
import wallapp.network.NetworkErrorBroadcaster
import wallapp.network.NetworkRefreshTriggerBroadcaster

@Suppress("OPT_IN_USAGE")
class RemoteApiEncryptionConfigDefault(
    private val remoteApiSecretManager: RemoteApiSecretManager,
    private val remoteEndpointsSpecRepository: RemoteEndpointsSpecRepository,
    networkRefreshTriggerBroadcaster: NetworkRefreshTriggerBroadcaster,
    private val firebaseStorageDownloader: FirebaseStorageDownloader,
    private val networkErrorBroadcaster: NetworkErrorBroadcaster,
    private val dataRepository: DataRepository,
    coroutineScopeIo: CoroutineScope,
) : RemoteApiEncryptionConfig {

    private val encryptionKeyFilename: String
        get() = "key1"

    // key1 lives in Firebase Storage, which the R2 delivery path never provisions. The catalog is
    // served as plaintext, so no key is required and callers must not wait on [key] to become
    // non-blank -- it never will.
    override val requiresEncryptionKey: Boolean
        get() = false

    private val _key = MutableStateFlow("")
    override val key: StateFlow<String>
        get() = _key.asStateFlow()
    override val initializationVector: StateFlow<ByteArray>
        get() = MutableStateFlow(
            byteArrayOf(47, -93, 98, 49, 107, 77, -74, 68, -17, -105, 89, 86)
        )

    private val remoteEndpointsSpec: RemoteEndpointsSpec
        get() = remoteEndpointsSpecRepository.remoteEndpointsSpec

    init {
        networkRefreshTriggerBroadcaster.keyRefresh.flatMapLatest {
            Log.d("[NRW-F] rAEC, refresh")
            val path = remoteEndpointsSpec.base + encryptionKeyFilename
            firebaseStorageDownloader.downloadFile(path, appendData = false)
        }.onEach { downloadState ->
            when (downloadState) {
                is DownloadState.Error -> {
                    // Only reachable when something still requests a Key refresh. Leaving [key]
                    // blank is safe on the plaintext path; see [requiresEncryptionKey].
                    Log.d("[NRW-F] rAEC, key1 unavailable -> continuing with empty key (plaintext R2 path)")
                }
                is DownloadState.Success -> {
                    Log.d("[NRW-F] rAEC, download success")
                    val dataBlob = dataRepository.getDataBlob(downloadState.dataHandle)
                    val asString = dataBlob?.byteArray?.decodeToString()
                    if (asString != null) {
                        _key.value = remoteApiSecretManager.getDeobfuscatedKey(asString)
                    } else {
                        networkErrorBroadcaster.reportNetworkError("rAECd") // decode error
                    }
                }
                else -> { /* no-op */ }
            }
        }.launchIn(coroutineScopeIo)
    }
}
