package wallapp.image.cache


interface ImageCacheKeyManager {

    fun add(keyItem: ImageCacheKeyItem)

    fun isCached(model: Any, width: Int, height: Int): Boolean

    fun getBestCached(model: Any, width: Int?, height: Int?): ImageCacheKeyItem?

    fun getAllCached(model: Any): List<ImageCacheKeyItem>?
}


object ImageCacheKeyManagerNoOp : ImageCacheKeyManager {
    override fun add(keyItem: ImageCacheKeyItem) { }

    override fun isCached(model: Any, width: Int, height: Int): Boolean = false

    override fun getBestCached(model: Any, width: Int?, height: Int?): ImageCacheKeyItem? = null

    override fun getAllCached(model: Any): List<ImageCacheKeyItem>? = null
}