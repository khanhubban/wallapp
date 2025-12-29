package wallapp.image.sized

import wallapp.image.sized.SizedImage.Showcase


/**
 * Predefined image sizes.
 * [key]: the key entry in the data map for the URL for an image at this size
 *
 * Note: when adding items, consider if an existing item can be reused. As an example, the Explore
 * feature carousel reuses the [Showcase] item.
 */
enum class SizedImage(
    val key: String,
    // Apply the Imgix crop parameter to the image URL
    val applyCrop: Boolean = true,
) {

    DownloadableWallpaperHd("dhd"),
    DownloadableWallpaperSd("dsd"),

    FullScreen("fs"),

    // The wallpaper showcase
    Showcase("s"),
    // Exhibit items at the top of the Explore feed. These are different from [Showcase], as they
    // are typically square so as to work with the parallax effect.
    Exhibit("e", applyCrop = false),

    WallpaperFeedSingle("wfs"),
    WallpaperFeedTrack("wft"),
    WallpaperCollectionSmallLayer0("wcs0"),
    WallpaperCollectionSmallLayer1("wcs1"),
    WallpaperCollectionSmallLayer2("wcs2"),
    WallpaperCollectionLargeLayer0("wcl0"),
    WallpaperCollectionLargeLayer1("wcl1"),
    WallpaperCollectionLargeLayer2("wcl2"),

    ArtistSmall("as"),
    ArtistMedium("am"),

    ;

    companion object {
        val Preset = WallpaperFeedSingle

        fun from(key: String): SizedImage {
            require(entries.map { it.key }.distinct().size == entries.size) {
                "Duplicate keys found in SizedImage"
            }
            
            return entries.firstOrNull { it.key == key } ?: Preset
        }
    }
}