package wallapp.data.content.media

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import wallapp.content.model.Wallpaper
import wallapp.data.wallpaper.StaticWallpaperSize
import wallapp.data.wallpaper.sizedImage
import wallapp.image.Image
import wallapp.image.ImageOptions
import wallapp.image.ImageSize
import wallapp.image.sized.SizedImage
import wallapp.image.sized.SizedImageMapper
import wallapp.media.model.MediaHolder
import wallapp.mediamap.MediaMapGetResult
import wallapp.mediamap.MediaMapRepository
import wallapp.pixel.image.ImageViewSpec
import wallapp.pixel.image.ImageViewState
import wallapp.resources.image.ImageRepository
import wallapp.string.quote

class ContentMediaRepositoryDefault(
    private val mediaMapRepository: MediaMapRepository,
    private val sizedImageMapper: SizedImageMapper,
    private val imageRepository: ImageRepository,
) : ContentMediaRepository {

    companion object {
        private val Log = WallpaperMediaLog
    }

    override fun getImageViewState(
        mediaHolder: MediaHolder,
        sizedImage: SizedImage,
        imageViewSpec: ImageViewSpec,
    ): ImageViewState {
        val (image, imageSize) = getImageChecked(mediaHolder, sizedImage)

        return ImageViewState(
            image = image,
            viewSpec = imageViewSpec,
            imageSize = imageSize,
        ).also {
            val mappedImageViewSpec = sizedImageMapper.mapImageViewSpec(sizedImage)
            // This is a valid use case. As an example, the Explore carousel might reuse the
            // WallpaperShowcase size, because it's not worth creating a new [SizedImage] in such
            // a case. But this is worth logging so it's clear that images are being used that are
            // not perfectly sized to the UI element.
            if (mappedImageViewSpec != imageViewSpec) {
                Log.w("Warning: SizedImage ${sizedImage.toString().quote()} mapped ImageViewSpecs do not match.\n  image: $image\n  mediaHolder: $mediaHolder\n  $mappedImageViewSpec != $imageViewSpec")
            }
        }
    }

    private fun getMedia(
        mediaHolder: MediaHolder,
        sizedImage: SizedImage,
    ): MediaMapGetResult {
        return mediaMapRepository.getMedia(mediaHolder, sizedImage)
    }

    private fun getImageChecked(
        mediaHolder: MediaHolder,
        sizedImage: SizedImage,
    ): Pair<Image, ImageSize?> {
        return when (val media = getMedia(mediaHolder, sizedImage)) {
            is MediaMapGetResult.Success -> {
                val imageModel = media.imageModel
                val imageSize = media.imageSize

                val image = Image.from(
                    model = imageModel,
                    contentDescription = imageModel.contentDescription,
                    imageOptions = ImageOptions {
                        this.imageSize = imageSize
                    },
                    loadingModel = imageModel.loadingModel,
                )

                return image to imageSize
            }

            is MediaMapGetResult.NotFound -> {
                brokenImage to null
            }
        }
    }

    private val brokenImage: Image by lazy {
        val brokenImage = imageRepository.brokenImage
        Image.from(
            brokenImage,
            contentDescription = brokenImage.contentDescription,
            imageOptions = ImageOptions {
                this.tintColorToken = brokenImage.tintColorToken
            },
        )
    }

    override fun getWallpaperMedia(
        wallpaper: Wallpaper,
        staticWallpaperSize: StaticWallpaperSize,
    ): Flow<ContentMediaGetResult> {
        val sizedImage = staticWallpaperSize.sizedImage
        val mediaId = when (sizedImage) {
            SizedImage.DownloadableWallpaperHd -> wallpaper.downloadMedia.hdMediaId
            SizedImage.DownloadableWallpaperSd -> wallpaper.downloadMedia.sdMediaId
            else -> throw IllegalArgumentException("Unsupported sizedImage: $sizedImage")
        }
        val mediaHolder = MediaHolder(mediaId)

        val result = when (val mediaResult = mediaMapRepository.getMedia(mediaHolder, sizedImage)) {
            is MediaMapGetResult.Success -> {
                ContentMediaGetResult.Success(mediaResult.imageModel, mediaId)
            }

            is MediaMapGetResult.NotFound -> {
                Log.w("MediaMapGetResult.NotFound: $mediaResult")
                ContentMediaGetResult.Error("Url not found: ${wallpaper.id.name},${sizedImage.key}, mediaId: ${mediaId.id}")
            }
        }

        return flowOf(result)
    }
}
