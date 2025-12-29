package wallapp.image

import wallapp.image.ImageCacheable.Companion.CACHE_TYPE_ALL
import wallapp.image.ImageCacheable.Companion.CACHE_TYPE_DISK
import wallapp.image.ImageCacheable.Companion.CACHE_TYPE_MEMORY
import wallapp.image.ImageCacheable.Companion.CACHE_TYPE_NONE
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ImageCacheableTest {

    @Test
    fun `cacheType set to CACHE_TYPE_NONE verify results`() {
        val imageCacheable = object: ImageCacheable(){}
        imageCacheable.cacheType = CACHE_TYPE_NONE
        assertFalse(imageCacheable.supportsAnyCache())
        assertFalse(imageCacheable.supportsMemoryCache())
        assertFalse(imageCacheable.supportsDiskCache())
    }

    @Test
    fun `cacheType set to CACHE_TYPE_MEMORY verify results`() {
        val imageCacheable = object: ImageCacheable(){}
        imageCacheable.cacheType = CACHE_TYPE_MEMORY
        assertTrue(imageCacheable.supportsAnyCache())
        assertTrue(imageCacheable.supportsMemoryCache())
        assertFalse(imageCacheable.supportsDiskCache())
    }

    @Test
    fun `cacheType set to CACHE_TYPE_DISK verify results`() {
        val imageCacheable = object: ImageCacheable(){}
        imageCacheable.cacheType = CACHE_TYPE_DISK
        assertTrue(imageCacheable.supportsAnyCache())
        assertFalse(imageCacheable.supportsMemoryCache())
        assertTrue(imageCacheable.supportsDiskCache())
    }

    @Test
    fun `cacheType set to CACHE_TYPE_ALL verify results`() {
        val imageCacheable = object: ImageCacheable(){}
        imageCacheable.cacheType = CACHE_TYPE_ALL
        assertTrue(imageCacheable.supportsAnyCache())
        assertTrue(imageCacheable.supportsMemoryCache())
        assertTrue(imageCacheable.supportsDiskCache())
    }
}
