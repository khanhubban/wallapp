package wallapp.wallpaper.cache

/**
 * Only meant to be a bridge for iOS specific implementation of [WallpaperImageCache]
 */
interface BaseCache {

    fun putData(
        key: String,
        data: ByteArray,
        completionHandler: (Boolean) -> Unit
    )

    fun getData(
        key: String,
        completionHandler: (ByteArray?) -> Unit
    )

    fun isCached(key: String): Boolean

    fun clearCache()
}