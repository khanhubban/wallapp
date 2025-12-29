package wallapp.wallpaper.cache

interface BaseCacheSuspend {

    suspend fun putData(
        key: String,
        data: ByteArray
    ): Boolean

    suspend fun getData(
        key: String
    ): ByteArray?

    suspend fun isCached(key: String): Boolean

    suspend fun clearCache()
}