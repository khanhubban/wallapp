package wallapp.wallpaper.static

import wallapp.device.DeviceSpec
import wallapp.wallpaper.download.WallpaperDownloadManager

abstract class StaticWallpaperManagerDefault(
    protected val wallpaperDownloadManager: WallpaperDownloadManager,
    protected val deviceSpec: DeviceSpec,
) : StaticWallpaperManager {

    protected val deviceWidth: Int
        get() = deviceSpec.size.value.widthPx
    protected val deviceHeight: Int
        get() = deviceSpec.size.value.heightPx

    protected fun wallpaperWidth(matchScreenSize: Boolean): Int {
        return if (matchScreenSize) {
            deviceWidth
        } else {
            deviceWidth + (deviceWidth / 4)
        }
    }
    protected fun wallpaperHeight(matchScreenSize: Boolean): Int = deviceHeight
}