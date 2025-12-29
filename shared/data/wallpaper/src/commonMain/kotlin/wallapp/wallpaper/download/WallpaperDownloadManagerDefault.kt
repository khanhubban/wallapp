package wallapp.wallpaper.download

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import wallapp.annotation.VisibleForTesting
import wallapp.content.model.Id.RemixId
import wallapp.content.model.WallpaperRemix
import wallapp.data.content.media.ContentMediaGetResult
import wallapp.data.content.media.ContentMediaRepository
import wallapp.data.wallpaper.StaticWallpaperSize
import wallapp.di.Lazy
import wallapp.download.ActiveDownloadStatus
import wallapp.download.DownloadManager
import wallapp.log.Logger
import wallapp.system.photo.status.SystemPhotoStatus
import wallapp.wallpaper.cache.WallpaperImageCache
import wallapp.wallpaper.model.RemixIdSizeKey
import wallapp.wallpaper.saver.WallpaperSaverManager
import wallapp.wallpaper.systemphotostatus.WallpaperSystemPhotoStatusManager

@OptIn(ExperimentalCoroutinesApi::class)
class WallpaperDownloadManagerDefault(
    private val downloadManager: DownloadManager,
    private val contentMediaRepository: ContentMediaRepository,
    private val wallpaperDownloadEventManager: WallpaperDownloadEventManager,
    private val wallpaperImageCache: WallpaperImageCache,
    private val wallpaperSystemPhotoStatusManager: WallpaperSystemPhotoStatusManager,
    wallpaperSaverManagerLazy: Lazy<WallpaperSaverManager>,
    coroutineScopeMain: CoroutineScope,
    private val coroutineScopeIo: CoroutineScope,
) : WallpaperDownloadManager {

    companion object {
        val Log = Logger("WallpaperDownload")
    }

    private val wallpaperSaverManager by lazy { wallpaperSaverManagerLazy.get() }
    private val wallpaperDownloadUrlCache = WallpaperDownloadUrlCache()

    @VisibleForTesting
    var testUrl: String?
        get() = testUrlFlow.value
        set(value) {
            testUrlFlow.value = value
        }
    private val testUrlFlow: MutableStateFlow<String?> = MutableStateFlow(null)

    private val newDownloadChannel =
        MutableSharedFlow<Flow<Map<RemixIdSizeKey, WallpaperDownloadState>>?>(extraBufferCapacity = 1)
    private val allDownloadStatesProcessedFlow = newDownloadChannel
        .onEach { Log.d("new flow: $it") }
        .filterNotNull()
        .flatMapMerge { it }
        .scan(emptyMap<RemixIdSizeKey, WallpaperDownloadState>()) { acc, map ->
            acc + map
        }
        .onEach { Log.d("scanned state: $it") }

    private val allDownloadStates = allDownloadStatesProcessedFlow.stateIn(
        coroutineScopeMain,
        SharingStarted.Eagerly,
        emptyMap()
    )

    override val activeDownloadsStatus: Flow<ActiveDownloadStatus>
        get() = downloadManager.activeDownloadsStatus

    private fun getWallpaperDownloadGetResult(
        wallpaper: WallpaperRemix,
        staticWallpaperSize: StaticWallpaperSize
    ): Flow<ContentMediaGetResult> = contentMediaRepository.getWallpaperMedia(wallpaper, staticWallpaperSize)

    override fun downloadStaticWallpaper(
        wallpaper: WallpaperRemix,
        staticWallpaperSize: StaticWallpaperSize,
        saveToSystemGallery: Boolean,
    ): Flow<WallpaperDownloadState> {
        val remixIdSizeKey = RemixIdSizeKey(wallpaper.id, staticWallpaperSize)
        if (allDownloadStates.value[remixIdSizeKey]?.isInProgress() == true) {
            return getWallpaperDownloadState(wallpaper.id, staticWallpaperSize)
        }

        val getResultFlow = getWallpaperDownloadGetResult(wallpaper, staticWallpaperSize)

        val downloadFlow = getResultFlow.flatMapLatest { getResult ->
            when (getResult) {
                is ContentMediaGetResult.Success -> {
                    val imageUrl = getResult.imageModel.url
                    wallpaperDownloadUrlCache.put(wallpaper.id, staticWallpaperSize, imageUrl)
                    downloadManager.downloadUrl(imageUrl)
                        .map { downloadState ->
                            val wallpaperDownloadState = downloadState
                                .mapToWallpaperDownloadState(wallpaper.id, staticWallpaperSize)
                            if (wallpaperDownloadState is WallpaperDownloadState.Success) {
                                cacheDownloadedWallpaper(
                                    wallpaperDownloadState,
                                    wallpaper.id,
                                    staticWallpaperSize,
                                )
                                if (saveToSystemGallery) {
                                    trySaveWallpaperToGallery(wallpaper, wallpaperDownloadState)
                                }
                            }
                            wallpaperDownloadState
                        }
                }
                is ContentMediaGetResult.Error -> flow {
                    emit(
                        WallpaperDownloadState.Error(
                            wallpaper.id,
                            url = null,
                            staticWallpaperSize = staticWallpaperSize,
                            message = getResult.errorMessage,
                        )
                    )
                }
            }
        }

        coroutineScopeIo.launch {
            Log.d("updateWallpaperState: $remixIdSizeKey")
            newDownloadChannel.emit(downloadFlow.map { mapOf(remixIdSizeKey to it) })
        }

        return getWallpaperDownloadState(wallpaper.id, staticWallpaperSize)
    }

    override fun getWallpaperDownloadState(
        wallpaperId: RemixId,
        staticWallpaperSize: StaticWallpaperSize?
    ): Flow<WallpaperDownloadState> {
        return allDownloadStates.map { map ->
            val remixIdSizeKey = RemixIdSizeKey(wallpaperId, staticWallpaperSize)
            map[remixIdSizeKey] ?: WallpaperDownloadState.None(
                wallpaperId,
                staticWallpaperSize,
            )
        }.distinctUntilChanged()
    }

    override fun cancelWallpaperDownload(
        wallpaper: WallpaperRemix,
        staticWallpaperSize: StaticWallpaperSize
    ) {
        val imageUrl = wallpaperDownloadUrlCache.get(wallpaper.id, staticWallpaperSize) ?: return
        downloadManager.cancelDownload(imageUrl)
    }

    private fun cacheDownloadedWallpaper(
        wallpaperDownloadState: WallpaperDownloadState.Success,
        wallpaperId: RemixId,
        staticWallpaperSize: StaticWallpaperSize,
    ) {
        Log.d("cacheDownloadedWallpaper (${wallpaperImageCache.enabled}) $wallpaperId")
        if (wallpaperImageCache.enabled) {
            coroutineScopeIo.launch {
                wallpaperImageCache.putStaticWallpaper(
                    wallpaperId,
                    staticWallpaperSize,
                    wallpaperDownloadState.dataHandle,
                ).also {
                    Log.d("Wallpaper Cached Status ${wallpaperId}: $it")
                }
            }
        }
    }

    private suspend fun trySaveWallpaperToGallery(
        wallpaper: WallpaperRemix,
        wallpaperDownloadState: WallpaperDownloadState.Success
    ) {
        val photoStatus = wallpaperSystemPhotoStatusManager.getSystemPhotoStatus(wallpaper.id)
        if (photoStatus is SystemPhotoStatus.ExistsInPhotoLibrary) {
            Log.i("Wallpaper already saved to gallery ${wallpaper.id}")
            return
        }
        wallpaperDownloadEventManager.registerWallpaperDownloadEvent(wallpaper.id)
        saveWallpaperToGallery(wallpaper, wallpaperDownloadState)
    }

    private fun saveWallpaperToGallery(
        wallpaper: WallpaperRemix,
        wallpaperDownloadState: WallpaperDownloadState.Success
    ) {
        coroutineScopeIo.launch {
            wallpaperSaverManager.saveWallpaper(
                wallpaperDownloadState,
                wallpaper,
                "WallApp",
            ).also {
                Log.d("saveWallpaperToGallery :- ${wallpaper.id} $it")
            }
        }
    }

}