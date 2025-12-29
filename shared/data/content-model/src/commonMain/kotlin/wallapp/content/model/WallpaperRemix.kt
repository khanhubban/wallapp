package wallapp.content.model

import wallapp.content.model.Id.ArtistId
import wallapp.content.model.Id.CategoryId
import wallapp.content.model.Id.RemixId

typealias Wallpaper = WallpaperRemix

/**
 * Definitions for assets that make a single wallpaper.
 */
sealed class WallpaperRemix : WallpaperItem {
    abstract override val id: RemixId
    abstract val label: String
    abstract val collectionLabel: String
    abstract val categoryId: CategoryId
    abstract val isSingle: Boolean
    abstract val isDark: Boolean
    /**
     * The [ColorShade] of the top of the wallpaper. Used to determine text / button colors that
     * should be used on top of the wallpaper.
     */
    abstract val topColorShade: ColorShade?
    abstract val previewImages: WallpaperRemixPreviewImages
    abstract val downloadMedia: WallpaperDownloadMedia
    abstract val slugs: List<String>?
    abstract val isAiEnhanced: Boolean
    abstract val isFree: Boolean

    val isTrack: Boolean
        get() = !isSingle
    val isInCollection: Boolean
        get() = isTrack
}

/**
 *
 */
data class WallpaperRemixParallax(
    override val id: RemixId,
    override val label: String,
    override val collectionLabel: String = label,
    override val artistId: ArtistId,
    val portraitHorizontalAlignment: Float = 0.5f,
    val portraitVerticalAlignment: Float = 0.5f,
    override val categoryId: CategoryId = CategoryId(""),
    override val isSingle: Boolean = true,
    override val isDark: Boolean = false,
    override val topColorShade: ColorShade? = null,
    override val previewImages: WallpaperRemixPreviewImages,
    override val downloadMedia: WallpaperDownloadMedia,
    override val slugs: List<String>? = null,
    override val isAiEnhanced: Boolean = false,
    override val isFree: Boolean = false,
) : WallpaperRemix() {

    override fun equals(other: Any?): Boolean {
        if (other !is WallpaperRemixParallax) return false
        return this.id == other.id
                && this.downloadMedia == other.downloadMedia
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

data class WallpaperRemixMock(
    override val id: RemixId,
    override val label: String = id.name,
    override val artistId: ArtistId = ArtistId.Preset,
    override val previewImages: WallpaperRemixPreviewImages = WallpaperRemixPreviewImages.Preset,
    override val downloadMedia: WallpaperDownloadMedia = WallpaperDownloadMedia.Preset,
    override val categoryId: CategoryId = CategoryId(""),
    override val isSingle: Boolean = true,
    override val isDark: Boolean = false,
    override val topColorShade: ColorShade? = null,
    override val slugs: List<String>? = null,
    override val isAiEnhanced: Boolean = false,
    override val isFree: Boolean = false,
): WallpaperRemix() {
    override val collectionLabel: String
        get() = label
}