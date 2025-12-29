package wallapp.image.cache


open class ImageCacheKeyManagerDefault : ImageCacheKeyManager {

    protected val items = mutableSetOf<ImageCacheKeyItem>()

    fun getCached(model: Any, width: Int, height: Int): ImageCacheKeyItem? =
        getAllCached(model)?.getExactCached(width, height)

    override fun add(keyItem: ImageCacheKeyItem) {
        items.add(keyItem)
//            .also {
//                if (it) {
//                    Log.d("ImageCacheKeyManager: added $keyItem")
//                } else {
//                    Log.v("ImageCacheKeyManager: already added $keyItem")
//                }
//            }
    }

    override fun isCached(model: Any, width: Int, height: Int): Boolean =
        getCached(model, width, height) != null

    override fun getBestCached(model: Any, width: Int?, height: Int?): ImageCacheKeyItem? {
        if (width == null || height == null) return null
        return getAllCached(model)?.getBestCached(width, height)
    }

    override fun getAllCached(model: Any): List<ImageCacheKeyItem>? {
        return items.filter { it.key == model }.ifEmpty { null }
    }
}