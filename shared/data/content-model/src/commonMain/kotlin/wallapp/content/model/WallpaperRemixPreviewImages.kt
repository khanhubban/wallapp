 package wallapp.content.model

import wallapp.media.model.MediaHolder

 data class WallpaperRemixPreviewImages(
     val mediaHolder: MediaHolder,
) {
    companion object {
        val Preset by lazy { WallpaperRemixPreviewImages(MediaHolder.Preset) }
    }
}