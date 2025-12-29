package wallapp.wallpaper.current

import wallapp.content.model.WallpaperId
import wallapp.data.wallpaper.StaticWallpaperSize
import wallapp.system.wallpaper.SystemWallpaperDestination
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class LastWallpaperDataTest {

    @Test fun `to and from valid LastWallpaperData 2 variables`() {
        val wallpaperId = WallpaperId("id~1234")
        val systemWallpaperId = 1
        val destination = SystemWallpaperDestination.LockScreen
        val staticWallpaperSize: StaticWallpaperSize? = null
        val lastWallpaperData = LastWallpaperData(wallpaperId, staticWallpaperSize, systemWallpaperId, destination)

        val data = lastWallpaperData.data
        val fromData = LastWallpaperData.from(data, destination)
        assertEquals(lastWallpaperData, fromData)
    }

    @Test fun `to and from valid LastWallpaperData 3 variables`() {
        val wallpaperId = WallpaperId("id~1234")
        val systemWallpaperId = 1
        val staticWallpaperSize = StaticWallpaperSize.FullResolution
        val destination = SystemWallpaperDestination.LockScreen
        val lastWallpaperData = LastWallpaperData(wallpaperId, staticWallpaperSize, systemWallpaperId, destination)

        val data = lastWallpaperData.data
        val fromData = LastWallpaperData.from(data, destination)
        assertEquals(lastWallpaperData, fromData)
    }

    @Test fun `empty string returns null`() {
        assertNull(LastWallpaperData.from("", SystemWallpaperDestination.LockScreen))
    }
}