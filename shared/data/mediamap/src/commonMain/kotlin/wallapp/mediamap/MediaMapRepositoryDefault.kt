package wallapp.mediamap

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import wallapp.coroutine.CoroutineScopes
import wallapp.image.ImageModel
import wallapp.image.bucket.ImageBucketSpec
import wallapp.image.size.ImageSizeMapper
import wallapp.image.sized.SizedImage
import wallapp.initialization.InitializationState
import wallapp.media.model.MediaHolder
import wallapp.media.model.MediaId
import wallapp.media.network.model.NetworkMediaData
import wallapp.media.network.model.NetworkMediaMapResult
import wallapp.media.network.repository.NetworkMediaMapRepository
import wallapp.network.NetworkConnectionState
import wallapp.network.NetworkErrorBroadcaster
import wallapp.network.NetworkRefreshTriggerBroadcaster
import wallapp.network.NetworkState
import wallapp.remotecontent.RemoteServerContentCache
import wallapp.string.quote
import wallapp.util.combine

@Suppress("OPT_IN_USAGE")
class MediaMapRepositoryDefault(
    private val config: MediaMapRepositoryConfig,
    private val networkMediaMapRepository: NetworkMediaMapRepository,
    private val networkState: NetworkState,
    private val imageSizeMapper: ImageSizeMapper,
    networkRefreshTriggerBroadcaster: NetworkRefreshTriggerBroadcaster,
    private val remoteServerContentCache: RemoteServerContentCache,
    private val networkErrorBroadcaster: NetworkErrorBroadcaster,
    private val coroutineScopes: CoroutineScopes,
) : MediaMapRepository {

    companion object {
        val Log = MediaMapLogger
    }

    // Exposed for testing
    val currentImageBucketSpec: StateFlow<ImageBucketSpec?>
        get() = config.imageBucketSpec

    private val networkConnectionState: StateFlow<NetworkConnectionState>
        get() = networkState.networkConnectionState

    private val dataSourceId: MutableStateFlow<String?> = MutableStateFlow(null)

    private val networkMediaData: Flow<NetworkMediaData> by lazy {
        remoteServerContentCache.mediaMap
            .onEach { Log.d("[NCache] mMR: ${it.isNotBlank()}") }
            .filter { it.isNotBlank() }
            .map { contentString ->
                contentString.let { NetworkMediaData.fromJson(contentString) }
            }
    }

    val mediaMapFromNetwork = MutableStateFlow<Map<MediaId, MediaMap>?>(null)

    val mediaMapFromCache: StateFlow<Map<MediaId, MediaMap>?> by lazy {
        networkMediaData
            .map { networkMediaData: NetworkMediaData ->
                MediaMapMapper.mapToMediaMap(networkMediaData.mediaMap)
                    .also {
                        Log.i("mediaMap: ${it.size}")
                    }
            }
            .stateIn(
                scope = coroutineScopes.io,
                started = SharingStarted.Eagerly,
                initialValue = null,
            )
    }

    override val initializationState: StateFlow<InitializationState> by lazy {
        combine(mediaMapFromCache, networkConnectionState) { mediaMap, networkConnectionState ->
            when {
                !mediaMap.isNullOrEmpty() -> InitializationState.Ready
                (networkConnectionState == NetworkConnectionState.Disconnected
                        || networkConnectionState == NetworkConnectionState.ConnectionNoInternet) ->
                            InitializationState.RequiresNetwork
                else -> InitializationState.Uninitialized
            }
        }
            .onEach { Log.i("initializationState: $it") }
            .stateIn(
                scope = coroutineScopes.io,
                started = SharingStarted.Eagerly,
                initialValue = InitializationState.Uninitialized
            )
    }

    override val isReady: StateFlow<Boolean> by lazy {
        initializationState.map { it == InitializationState.Ready }
            .onEach { Log.i("isReady: $it") }
            .stateIn(coroutineScopes.io, started = SharingStarted.Eagerly, false)
    }

    private val imageBucketSpec: ImageBucketSpec
        get() = config.imageBucketSpec.value!!

    override fun getMedia(
        mediaHolder: MediaHolder,
        sizedImage: SizedImage,
    ): MediaMapGetResult {
        require(isReady.value) { "MediaMapRepository is not ready" }

        val mediaId = mediaHolder.mediaId

        val mediaMap = mediaMapFromCache.value?.get(mediaId)
        if (mediaMap == null) {
            Log.w("MediaMap entry missing for mediaId: $mediaId")
            return MediaMapGetResult.NotFound.NotFoundMediaId(mediaId, sourceId = dataSourceId.value)
        }

        val sizedImageModel = mediaMap[sizedImage]
        if (sizedImageModel == null) {
            Log.w("MediaMap not found for mediaId: $mediaId, Requested SizedImage: $sizedImage, mediaMap.size: ${mediaMap.size}, mediaMap.keys: ${mediaMap.keys}" +
                    "\n  images: ${mediaMap.values}")
            return MediaMapGetResult.NotFound.NotFoundSizedImage(
                sourceId = dataSourceId.value,
                mediaId = mediaId,
                missingSizedImage = sizedImage,
                validSizedImages = mediaMap.keys.toList(),
            )
        }

        val imageSize = imageSizeMapper.getImageSize(sizedImage, imageBucketSpec)

        val imageModel = ImageModel.from(
            sizedImageModel,
            contentDescription = mediaHolder.contentDescription,
            loadingModel = mediaHolder.imageHash,
        )

        return MediaMapGetResult.Success(imageModel, imageSize)
    }

    init {
        networkRefreshTriggerBroadcaster.mediaMapRefresh.flatMapLatest {
            Log.d("[NRW-F] mMR, refresh")
            networkMediaMapRepository.fetchNetworkMediaMap(forceRefresh = true)
                .onEach { result ->
                    when (result) {
                        is NetworkMediaMapResult.Success -> {
                            Log.i("Fetched network media data, source: ${result.sourceId.quote()}")
                        }

                        is NetworkMediaMapResult.Error -> {
                            Log.w("Error fetching network media data: ${result.errorMessage}")
                        }
                    }
                }
        }.onEach { result ->
            Log.i("networkMediaMapResult emitted $result")
            val mediaData: NetworkMediaData?
            if (result is NetworkMediaMapResult.Success) {
                dataSourceId.value = result.sourceId
                mediaData = result.networkMediaData
            } else {
                dataSourceId.value = null
                mediaData = null
            }
            if (mediaData == null) {
                if (remoteServerContentCache.mediaMap.value.isBlank()) {
                    Log.w("MediaMap data is not fetched")
                    networkErrorBroadcaster.reportNetworkError("mMR")
                }
            } else {
                mediaMapFromNetwork.value = MediaMapMapper.mapToMediaMap(mediaData.mediaMap)
                remoteServerContentCache.mediaMap.value = mediaData.exportString
            }
        }.launchIn(coroutineScopes.io)
    }
}
