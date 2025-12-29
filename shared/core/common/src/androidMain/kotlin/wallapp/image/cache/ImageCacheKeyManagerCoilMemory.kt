package wallapp.image.cache

import coil.ImageLoader
import coil.memory.MemoryCache

class ImageCacheKeyManagerCoilMemory(
    private val imageLoader: ImageLoader,
) : ImageCacheKeyManagerDefault() {

    private val ImageCacheKeyItem.isCachedInMemory: Boolean
        get() {
            if (this.key !is MemoryCache.Key) return false
            val key = this.key as MemoryCache.Key
            return imageLoader.memoryCache?.keys?.contains(key) != null
        }

    override fun getAllCached(model: Any): List<ImageCacheKeyItem>? {
        return super.getAllCached(model)
            ?.filter { it.isCachedInMemory }
    }
}