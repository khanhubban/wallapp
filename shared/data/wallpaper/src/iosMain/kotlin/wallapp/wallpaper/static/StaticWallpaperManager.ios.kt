package wallapp.wallpaper.static

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import wallapp.content.model.WallpaperRemix
import wallapp.data.wallpaper.StaticWallpaperSize
import wallapp.device.DeviceSpec
import wallapp.resources.string.StringRepository
import wallapp.wallpaper.download.WallpaperDownloadManager
import wallapp.wallpaper.download.WallpaperDownloadState
import wallapp.wallpaper.model.RemixIdSizeKey
import wallapp.wallpaper.saver.WallpaperSaverManager

class StaticWallpaperManagerIos(
    wallpaperDownloadManager: WallpaperDownloadManager,
    deviceSpec: DeviceSpec,
    private val wallpaperSaverManager: WallpaperSaverManager,
    private val strings: StringRepository,
    private val coroutineScopeIo: CoroutineScope,
) : StaticWallpaperManagerDefault(wallpaperDownloadManager, deviceSpec) {

    private val photoAlbumName: String
        get() = strings.photoAlbumName

    override val canSetWallpaper: Boolean
        get() = false

//    override fun setWallpaper(
//        wallpaperRemix: WallpaperRemix,
//        staticWallpaperSize: StaticWallpaperSize,
//    ) {
//        require(canSetWallpaper) { "Cannot set wallpaper on iOS" }
//    }

    override val currentSettingWallpaper: StateFlow<Set<RemixIdSizeKey>> = MutableStateFlow(emptySet())

    override fun setWallpaper(
        wallpaperDownloadState: WallpaperDownloadState.Success,
        wallpaperRemix: WallpaperRemix,
    ) {
        coroutineScopeIo.launch {
            wallpaperSaverManager.saveWallpaper(
                wallpaperDownloadState = wallpaperDownloadState,
                wallpaperRemix = wallpaperRemix,
                photoAlbumName = photoAlbumName,
            )
        }
    }

    override fun setWallpaperWithRemixAndSize(
        wallpaperRemix: WallpaperRemix,
        staticWallpaperSize: StaticWallpaperSize
    ) {
        // We are not setting wallpaper on iOS
    }
}