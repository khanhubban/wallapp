package wallapp.data.wallpaper

import kotlinx.serialization.Serializable
import wallapp.image.sized.SizedImage
import wallapp.string.quote

@Serializable
sealed class StaticWallpaperSize(val key: String) {

    data object FullResolution : StaticWallpaperSize(key = "resfull")

    data object StandardResolution : StaticWallpaperSize(key = "resSD") {
        val width: Int = 1080
        val height: Int = 1920
    }

    /**
     * Attempt to crop the image to fit the screen exactly.
     */
    data object MatchScreenSize : StaticWallpaperSize(key = "resScreenMatch")

    /**
     * Attempt to crop the image to fit the screen exactly, but add an amount of padding to the
     * edges of the image.
     */
    data object MatchScreenSizeWithPadding : StaticWallpaperSize(key = "resScreenMatchWithPadding")

    companion object {

        fun fromKey(key: String): StaticWallpaperSize {
            return when (key) {
                FullResolution.key -> FullResolution
                StandardResolution.key -> StandardResolution
                MatchScreenSize.key -> MatchScreenSize
                MatchScreenSizeWithPadding.key -> MatchScreenSizeWithPadding
                else -> throw IllegalArgumentException("Unknown key: ${key.quote()}")
            }
        }

    }
}

val StaticWallpaperSize.sizedImage: SizedImage
    get() = when (this) {
        StaticWallpaperSize.FullResolution -> SizedImage.DownloadableWallpaperHd
        StaticWallpaperSize.MatchScreenSize -> TODO()
        StaticWallpaperSize.MatchScreenSizeWithPadding -> TODO()
        StaticWallpaperSize.StandardResolution -> SizedImage.DownloadableWallpaperSd
    }