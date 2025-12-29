package wallapp.image.sized

import wallapp.image.ImageViewSpecFactory
import wallapp.pixel.image.ImageViewSpec

class SizedImageMapperDefault(
    val imageViewSpecFactory: ImageViewSpecFactory,
) : SizedImageMapper {

    override fun mapImageViewSpec(sizedImage: SizedImage): ImageViewSpec {
        return when (sizedImage) {
            SizedImage.DownloadableWallpaperHd -> throw IllegalArgumentException("DownloadableWallpaperHd does not have an ImageViewSpec")
            SizedImage.DownloadableWallpaperSd -> throw IllegalArgumentException("DownloadableWallpaperSd does not have an ImageViewSpec")

            SizedImage.FullScreen -> imageViewSpecFactory.fullScreenImageViewSpec

            SizedImage.Showcase -> imageViewSpecFactory.wallpaperShowcaseImageViewSpec

            SizedImage.Exhibit -> imageViewSpecFactory.carouselHighlightImageViewSpec

            SizedImage.WallpaperFeedTrack -> imageViewSpecFactory.wallpaperFeedTrackImageViewSpec
            SizedImage.WallpaperFeedSingle -> imageViewSpecFactory.wallpaperFeedSingleImageViewSpec
            SizedImage.WallpaperCollectionSmallLayer0 -> imageViewSpecFactory.collectionPreviewSmallImageViewSpec(0)
            SizedImage.WallpaperCollectionSmallLayer1 -> imageViewSpecFactory.collectionPreviewSmallImageViewSpec(1)
            SizedImage.WallpaperCollectionSmallLayer2 -> imageViewSpecFactory.collectionPreviewSmallImageViewSpec(2)
            SizedImage.WallpaperCollectionLargeLayer0 -> imageViewSpecFactory.collectionPreviewFullImageViewSpec(0)
            SizedImage.WallpaperCollectionLargeLayer1 -> imageViewSpecFactory.collectionPreviewFullImageViewSpec(1)
            SizedImage.WallpaperCollectionLargeLayer2 -> imageViewSpecFactory.collectionPreviewFullImageViewSpec(2)

            SizedImage.ArtistSmall -> imageViewSpecFactory.artistProfileImageSmallImageViewSpec
            SizedImage.ArtistMedium -> imageViewSpecFactory.artistProfileImageMediumImageViewSpec
        }
    }
}
