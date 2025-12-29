package wallapp.wallpaper.systemphotostatus

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import wallapp.content.model.Id.RemixId
import wallapp.system.photo.SystemPhotoId
import wallapp.system.photo.status.SystemPhotoStatus
import kotlin.test.Test
import kotlin.test.assertEquals

class WallpaperSystemPhotoStatusCacheEntryTest {

    private val testEntry = WallpaperSystemPhotoStatusCacheEntry(
        id = RemixId("testId"),
        status = SystemPhotoStatus.ExistsInPhotoLibrary(SystemPhotoId("sysId1"))
    )

    private val testListItems = listOf(
        WallpaperSystemPhotoStatusCacheEntry(
            id = RemixId("testId1"),
            status = SystemPhotoStatus.ExistsInPhotoLibrary(SystemPhotoId("sysId1"))
        ),
        WallpaperSystemPhotoStatusCacheEntry(
            id = RemixId("testId2"),
            status = SystemPhotoStatus.ExistsInPhotoLibrary(SystemPhotoId("sysId2"))
        )
    )


    @Test
    fun testSerialization() = runTest {
        val json = testEntry.exportString

        val deserializedEntry = Json.decodeFromString(WallpaperSystemPhotoStatusCacheEntry.serializer(), json)

        assertEquals(testEntry.id, deserializedEntry.id)
        assertEquals(testEntry.status, deserializedEntry.status)
    }

    @Test
    fun testDeserialization() = runTest {
        val json = testEntry.exportString

        val deserializedEntry = WallpaperSystemPhotoStatusCacheEntry.from(json)

        assertEquals(testEntry.id, deserializedEntry.id)
        assertEquals(testEntry.status, deserializedEntry.status)
    }

    @Test
    fun testListSerialization() {
        val json = mapWallpaperSystemPhotoStatusCacheEntryToJsonString(testListItems)
        val deserializedEntries = Json.decodeFromString(ListSerializer(WallpaperSystemPhotoStatusCacheEntry.serializer()), json)

        assertEquals(testListItems.size, deserializedEntries.size)
        runBlocking {
            assertEquals(testListItems[0].id, deserializedEntries[0].id)
            assertEquals(testListItems[0].status, deserializedEntries[0].status)
            assertEquals(testListItems[1].id, deserializedEntries[1].id)
            assertEquals(testListItems[1].status, deserializedEntries[1].status)
        }
    }

    @Test
    fun testListDeserialization() {
        val json = mapWallpaperSystemPhotoStatusCacheEntryToJsonString(testListItems)
        val deserializedEntries = mapWallpaperSystemPhotoStatusCacheEntryFromJsonString(json)

        assertEquals(testListItems.size, deserializedEntries.size)
        runBlocking {
            assertEquals(testListItems[0].id, deserializedEntries[0].id)
            assertEquals(testListItems[0].status, deserializedEntries[0].status)
            assertEquals(testListItems[1].id, deserializedEntries[1].id)
            assertEquals(testListItems[1].status, deserializedEntries[1].status)
        }
    }

    @Test
    fun testListDeserialization_empty() {
        val deserializedEntries = mapWallpaperSystemPhotoStatusCacheEntryFromJsonString("")
        assertEquals(0, deserializedEntries.size)
    }
}