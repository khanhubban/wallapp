package wallapp.mediamap

import wallapp.image.sized.SizedImage
import wallapp.media.model.MediaId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MediaMapMapperTest {

    @Test
    fun knownKeysMapToTheirSizedImage() {
        val result = MediaMapMapper.mapToMediaMap(
            mapOf(1L to mapOf("wfs" to "https://cdn/real.webp", "e" to "https://cdn/banner.webp")),
        )
        val entry = result[MediaId(1L)]!!
        assertEquals("https://cdn/real.webp", entry[SizedImage.WallpaperFeedSingle]!!.url)
        assertEquals("https://cdn/banner.webp", entry[SizedImage.Exhibit]!!.url)
    }

    /**
     * Before fromOrNull, an unknown key resolved to Preset (== WallpaperFeedSingle) and overwrote
     * the real wfs entry. A single pipeline typo would have blanked the feed for every wallpaper.
     */
    @Test
    fun unknownKeyIsDroppedAndDoesNotClobberThePresetEntry() {
        val result = MediaMapMapper.mapToMediaMap(
            mapOf(1L to mapOf("wfs" to "https://cdn/real.webp", "wsc0" to "https://cdn/typo.webp")),
        )
        val entry = result[MediaId(1L)]!!
        assertEquals("https://cdn/real.webp", entry[SizedImage.WallpaperFeedSingle]!!.url)
        assertEquals(1, entry.size)
    }

    @Test
    fun fromOrNullRejectsUnknownKeys() {
        assertEquals(SizedImage.Exhibit, SizedImage.fromOrNull("e"))
        assertNull(SizedImage.fromOrNull("wsc0"))
    }
}
