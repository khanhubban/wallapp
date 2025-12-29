package wallapp.data.content.media

import kotlinx.coroutines.flow.Flow
import wallapp.content.model.Wallpaper
import wallapp.data.wallpaper.StaticWallpaperSize
import wallapp.image.ImageModel
import wallapp.image.sized.SizedImage
import wallapp.media.model.MediaHolder
import wallapp.pixel.image.ImageViewSpec
import wallapp.pixel.image.ImageViewState

interface ContentMediaRepository {

    fun getImageViewState(
        mediaHolder: MediaHolder,
        sizedImage: SizedImage,
        imageViewSpec: ImageViewSpec,
    ): ImageViewState

    /**
     * Returns the [ImageModel] to be used for content placed in the system gallery.
     */
    fun getWallpaperMedia(
        wallpaper: Wallpaper,
        staticWallpaperSize: StaticWallpaperSize,
    ): Flow<ContentMediaGetResult>
}