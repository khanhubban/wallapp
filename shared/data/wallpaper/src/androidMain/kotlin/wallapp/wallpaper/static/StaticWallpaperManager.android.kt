package wallapp.wallpaper.static

import android.graphics.Bitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import wallapp.bitmap.BitmapMapper.toBitmap
import wallapp.content.model.Id.RemixId
import wallapp.content.model.WallpaperRemix
import wallapp.data.DataRepository
import wallapp.data.wallpaper.StaticWallpaperSize
import wallapp.device.DeviceSpec
import wallapp.system.wallpaper.SystemWallpaperDestination
import wallapp.system.wallpaper.SystemWallpaperManager
import wallapp.wallpaper.cache.WallpaperImageCache
import wallapp.wallpaper.current.CurrentWallpaperManager
import wallapp.wallpaper.download.WallpaperDownloadManager
import wallapp.wallpaper.download.WallpaperDownloadState
import wallapp.wallpaper.model.RemixIdSizeKey

class StaticWallpaperManagerAndroid(
    private val systemWallpaperManager: SystemWallpaperManager,
    private val currentWallpaperManager: CurrentWallpaperManager,
    wallpaperDownloadManager: WallpaperDownloadManager,
    private val dataRepository: DataRepository,
    deviceSpec: DeviceSpec,
    private val wallpaperImageCache: WallpaperImageCache,
    private val coroutineScopeIo: CoroutineScope,
) : StaticWallpaperManagerDefault(wallpaperDownloadManager, deviceSpec) {

    override val canSetWallpaper: Boolean
        get() = true

//    override fun setWallpaper(
//        wallpaperRemix: WallpaperRemix,
//        staticWallpaperSize: StaticWallpaperSize,
//    ) {
//        coroutineScopeIo.launch {
//            Log.d("setWallpaper(remixId: ${wallpaperRemix.id}, staticWallpaperSize=$staticWallpaperSize):")
//
//            val getResult = wallpaperDownloadManager
//                .getWallpaperDownloadGetResult(wallpaperRemix, staticWallpaperSize)
//                .first()
//            if (getResult !is ContentMediaGetResult.Success) {
//                Log.d("getWallpaperDownloadGetResult() failed: $getResult")
//                return@launch
//            }
//            val sizedImageUrl = getResult.imageModel.url
//            Log.d("getWallpaperBitmap(staticWallpaperSize=$staticWallpaperSize): $sizedImageUrl")
//            val bitmap = imageFetcher.fetchBitmapByUrl(sizedImageUrl)
//
//            if (bitmap != null) {
//                setStaticWallpaper(
//                    id = wallpaperRemix.id,
//                    bitmap = bitmap,
//                    staticWallpaperSize = staticWallpaperSize,
//                    destination = SystemWallpaperDestination.Both,
//                )
//            }
//        }
//    }

    private val _currentSettingWallpaper = MutableStateFlow(emptySet<RemixIdSizeKey>())
    override val currentSettingWallpaper: StateFlow<Set<RemixIdSizeKey>>
        get() = _currentSettingWallpaper.asStateFlow()

    override fun setWallpaper(
        wallpaperDownloadState: WallpaperDownloadState.Success,
        wallpaperRemix: WallpaperRemix,
    ) {
        val remixIdSizeKey = RemixIdSizeKey(wallpaperRemix.id, wallpaperDownloadState.staticWallpaperSize)
        _currentSettingWallpaper.value += remixIdSizeKey
        coroutineScopeIo.launch {
            val dataBlob = dataRepository.getDataBlob(wallpaperDownloadState.dataHandle)
            if (dataBlob == null) {
                _currentSettingWallpaper.value -= remixIdSizeKey
                return@launch
            }
            
            val bitmap = dataBlob.byteArray.toBitmap()
            setStaticWallpaper(
                id = wallpaperDownloadState.id as RemixId,
                bitmap,
                staticWallpaperSize = wallpaperDownloadState.staticWallpaperSize,
                destination = SystemWallpaperDestination.Both,
            )
            _currentSettingWallpaper.value -= remixIdSizeKey
        }
    }

    override fun setWallpaperWithRemixAndSize(
        wallpaperRemix: WallpaperRemix,
        staticWallpaperSize: StaticWallpaperSize
    ) {
        val remixIdSizeKey = RemixIdSizeKey(wallpaperRemix.id, staticWallpaperSize)
        _currentSettingWallpaper.value += remixIdSizeKey
        coroutineScopeIo.launch {
            val dataHandle = wallpaperImageCache.getStaticWallpaper(wallpaperRemix.id, staticWallpaperSize) ?: run {
                _currentSettingWallpaper.value -= remixIdSizeKey
                return@launch
            }
            val dataBlob = dataRepository.getDataBlob(dataHandle) ?: run {
                _currentSettingWallpaper.value -= remixIdSizeKey
                return@launch
            }
            val bitmap = dataBlob.byteArray.toBitmap()
            setStaticWallpaper(
                id = wallpaperRemix.id,
                bitmap,
                staticWallpaperSize = staticWallpaperSize,
                destination = SystemWallpaperDestination.Both,
            )
            _currentSettingWallpaper.value -= remixIdSizeKey
        }
    }

    private suspend fun setStaticWallpaper(
        id: RemixId,
        bitmap: Bitmap,
        staticWallpaperSize: StaticWallpaperSize,
        destination: SystemWallpaperDestination,
    ) {
        currentWallpaperManager.updateCurrentWallpaper(id, staticWallpaperSize, destination) {
            systemWallpaperManager.setStaticWallpaper(bitmap, destination)
        }
    }
}