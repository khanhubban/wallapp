package wallapp.content.model

import wallapp.image.ImageSize
import wallapp.media.model.MediaId

data class WallpaperDownloadMedia(
    val hdImageSize: ImageSize,
    val hdMediaId: MediaId,
    val sdMediaId: MediaId,
) {
    companion object {
        val Preset = WallpaperDownloadMedia(
            hdImageSize = ImageSize(1920, 1080),
            hdMediaId = MediaId.Preset,
            sdMediaId = MediaId.Preset,
        )
    }
}
