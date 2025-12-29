package wallapp.wallpaper.history

import wallapp.content.model.Id
import kotlin.test.Test
import kotlin.test.assertEquals


internal class OrderedWallpaperTest {

    @Test fun `to and from Json`() {
        val item = OrderedWallpaper(
            remixId = Id.RemixId("remixId"),
//            designId = Id.DesignId("designId"),
            order = 87,
        )
        val json = item.exportString
        val item2 = OrderedWallpaper.fromExportString(json)
        assertEquals(item, item2)
    }
}