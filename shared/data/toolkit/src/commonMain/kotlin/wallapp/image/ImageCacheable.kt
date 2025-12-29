package wallapp.image

abstract class ImageCacheable {
    var cacheType: Int = CACHE_TYPE_ALL
    fun setNoCache() { cacheType = CACHE_TYPE_NONE }
    fun supportsAnyCache() = cacheType and CACHE_TYPE_NONE == 0
    fun supportsMemoryCache() = cacheType and CACHE_TYPE_MEMORY != 0
    fun supportsDiskCache() = cacheType and CACHE_TYPE_DISK != 0

    companion object {
        const val CACHE_TYPE_NONE = 0x01
        const val CACHE_TYPE_DISK = 0x02
        const val CACHE_TYPE_MEMORY = 0x04
        const val CACHE_TYPE_ALL = CACHE_TYPE_DISK or CACHE_TYPE_MEMORY
    }
}

