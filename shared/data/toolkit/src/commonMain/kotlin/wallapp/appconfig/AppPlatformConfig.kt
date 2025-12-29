package wallapp.appconfig

class AppPlatformConfig(
    val packageName: String,
    val mainActivityClassName: String,
    val wallpaperServiceClassName: String?,
) {
    val isLiveWallpaperAvailable: Boolean
        get() = wallpaperServiceClassName != null
}
