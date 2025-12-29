package wallapp.wallpaper.history

import wallapp.content.model.Id
import kotlin.test.Test
import kotlin.test.assertEquals

class OrderedWallpapersTest {

    @Test fun `OrderedWallpapers to and from Json`() {
        val orderedWallpapers = OrderedWallpapers(
            setOf(
                OrderedWallpaper(
                    Id.RemixId("remixId"),
                    0,
                ),
                OrderedWallpaper(
                    Id.RemixId("remixId3"),
                    3,
                ),
                OrderedWallpaper(
                    Id.RemixId("remixId2"),
                    2,
                ),
            )
        )

        val json = orderedWallpapers.exportString
        val fromJson = OrderedWallpapers.fromExportString(json)
        assertEquals(orderedWallpapers, fromJson)
    }
}