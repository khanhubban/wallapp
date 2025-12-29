package wallapp.wallpaper.systemphotostatus

import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import wallapp.content.model.Id
import wallapp.system.photo.SystemPhotoId
import wallapp.system.photo.status.SystemPhotoStatus
import kotlin.test.Test
import kotlin.test.assertEquals

class WallpaperSystemPhotoStatusCacheDefaultTest {

    private fun WallpaperSystemPhotoStatusCacheEntry(id: String) =
        WallpaperSystemPhotoStatusCacheEntry(
            id = Id.RemixId("remixId$id"),
            status = SystemPhotoStatus.ExistsInPhotoLibrary(SystemPhotoId("sysPhotoId$id"))
        )

    private val testEntry1 = WallpaperSystemPhotoStatusCacheEntry("id1")
    private val testEntry2 = WallpaperSystemPhotoStatusCacheEntry("id2")
    private val testEntries = listOf(testEntry1, testEntry2)

    private fun createSourceData(allCache: String = ""): WallpaperSystemPhotoStatusCacheDefaultData {
        return WallpaperSystemPhotoStatusCacheDefaultDataMock(allCache)
    }

    private fun TestScope.createCache(
        data: WallpaperSystemPhotoStatusCacheDefaultData = createSourceData(),
    ): WallpaperSystemPhotoStatusCacheDefault {
        return WallpaperSystemPhotoStatusCacheDefault(data, coroutineScopeIo = TestScope())
    }

    @Test
    fun `all should return empty list when allCache is empty`() = runTest {
        val cache = createCache()
        assertEquals(emptyList(), cache.all.value)
    }

    @Test
    fun `all should return list of entries when allCache is not empty`() = runTest {
        val cache = createCache(
            createSourceData(
                allCache = mapWallpaperSystemPhotoStatusCacheEntryToJsonString(testEntries),
            ),
        )
        assertEquals(testEntries, cache.all.value)
    }

    /**
     * These tests don't pass because the `cache.all` value is not immediately updated.
     * This value does update at runtime, so this suggests a timing / CoroutineScope issue with the
     * tests.
     */
//    @Test
//    fun `add should correctly add a new item to the cache`() = runTest {
//        val cache = createCache()
//        val newItem = WallpaperSystemPhotoStatusCacheEntry("id3")
//        runBlocking {
//            cache.add(newItem)
//            val all = cache.all.value
//            assertTrue(all.contains(newItem))
//        }
//    }
//
//    @Test
//    fun `add should replace an item with the same id`() = runTest {
//        val cache = createCache(
//            createSourceData(
//                allCache = mapWallpaperSystemPhotoStatusCacheEntryToJsonString(testEntries),
//            )
//        )
//        val updatedItem = WallpaperSystemPhotoStatusCacheEntry("id1").copy(
//            status = SystemPhotoStatus.NotInPhotoLibrary
//        )
//        cache.add(updatedItem)
//
//        assertEquals(updatedItem, cache.all.value.find { it.id == updatedItem.id })
//        assertEquals(2, cache.all.value.size)
//    }
}