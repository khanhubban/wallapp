package wallapp.image.cache

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull


internal class ImageCacheKeyItemKtTest {

    @Test
    fun `getBestCached() exact`() {
        val url = "https://example.com"

        val item1 = ImageCacheKeyItem(url, "key1", 400, 400)
        val item2 = ImageCacheKeyItem(url, "key2", 800, 800)
        val item3 = ImageCacheKeyItem(url, "key3", 900, 900)
        val item4 = ImageCacheKeyItem(url, "key4", 1000, 1000)
        val items = listOf(item1, item2, item3, item4)

        assertEquals(item3, items.getExactCached(900, 900))
        assertEquals(item3, items.getBestCached(900, 900))
    }

    @Test
    fun `getBestCached() closest`() {
        val url = "https://example.com"

        val item1 = ImageCacheKeyItem(url, "key1", 400, 400)
        val item2 = ImageCacheKeyItem(url, "key2", 900, 900)
        val item3 = ImageCacheKeyItem(url, "key3", 800, 800)
        val items = listOf(item1, item2, item3)

        assertNull(items.getExactCached(802, 802))
        assertEquals(item3, items.getBestCached(802, 802))
    }
}