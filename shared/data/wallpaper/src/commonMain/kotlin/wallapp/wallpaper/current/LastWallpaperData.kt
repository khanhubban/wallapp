package wallapp.wallpaper.current

import wallapp.content.model.WallpaperId
import wallapp.data.wallpaper.StaticWallpaperSize
import wallapp.system.wallpaper.SystemWallpaperDestination

data class LastWallpaperData(
    val remixId: WallpaperId,
    val staticWallpaperSize: StaticWallpaperSize?,
    val systemWallpaperId: Int?,
    val destination: SystemWallpaperDestination,
) {
    val data: String
        get() = if (staticWallpaperSize == null) {
            "${remixId.name}$Separator$systemWallpaperId"
        } else {
            "${remixId.name}$Separator${staticWallpaperSize.key}$Separator$systemWallpaperId"
        }

    val currentWallpaperInfo: CurrentWallpaperInfo
        get() = CurrentWallpaperInfo(remixId, staticWallpaperSize)

    companion object {

        const val Separator = "<>"

        internal fun unmap(string: String): Triple<WallpaperId, StaticWallpaperSize?, Int>? {
            if (string.isEmpty() || string.isBlank()) return null
            val split = string.split(Separator)
            val splitSize = split.size
            require(splitSize == 2 || splitSize == 3)
            val wallpaperId = WallpaperId(split[0])
            return when (splitSize) {
                2 -> {
                    val systemWallpaperId = split[1].toInt()
                    Triple(wallpaperId, null, systemWallpaperId)
                }
                3 -> {
                    val staticWallpaperSize = StaticWallpaperSize.fromKey(split[1])
                    val systemWallpaperId = split[2].toInt()
                    Triple(wallpaperId, staticWallpaperSize, systemWallpaperId)
                }
                else -> {
                    throw IllegalArgumentException("Invalid data: $string")
                }
            }
        }


        fun from(data: String, destination: SystemWallpaperDestination): LastWallpaperData? {
            val (wallpaperId, staticWallpaperSize, systemWallpaperId) = unmap(data) ?: return null
            return LastWallpaperData(
                remixId = wallpaperId,
                staticWallpaperSize = staticWallpaperSize,
                systemWallpaperId = systemWallpaperId,
                destination = destination,
            )
        }

    }
}
