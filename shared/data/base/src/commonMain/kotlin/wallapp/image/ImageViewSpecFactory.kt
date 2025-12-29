package wallapp.image

import wallapp.pixel.image.ImageViewSpec

interface ImageViewSpecFactory {

    val fullScreenImageViewSpec: ImageViewSpec

    val wallpaperFeedSingleImageViewSpec: ImageViewSpec
    val wallpaperFeedTrackImageViewSpec: ImageViewSpec
    val wallpaperShowcaseImageViewSpec: ImageViewSpec

    val carouselHighlightImageViewSpec: ImageViewSpec
    val artistSelectionBackgroundImageViewSpec: ImageViewSpec

    fun collectionPreviewSmallImageViewSpec(index: Int): ImageViewSpec
    fun collectionPreviewSmallImageViewSpecs(): List<ImageViewSpec>

    fun collectionPreviewFullImageViewSpec(index: Int): ImageViewSpec
    fun collectionPreviewFullImageViewSpecs(): List<ImageViewSpec>

    val artistProfileImageSmallImageViewSpec: ImageViewSpec
    val artistProfileImageMediumImageViewSpec: ImageViewSpec

    val collectionToolbarArtistProfileImageViewSpec: ImageViewSpec

    val wallpaperArtistProfileImageViewSpec: ImageViewSpec

    val artistPreviewProfileImageViewSpecOnboarding: ImageViewSpec

    val artistToolbarProfileImageViewSpec: ImageViewSpec

    val artistsProfileImageViewSpec: ImageViewSpec

    val accountProfileImageViewSpec: ImageViewSpec

    val homeTopBarProfileImageViewSpec: ImageViewSpec
    val homeArtistPreviewImageViewSpec: ImageViewSpec

    val accountOverviewProfileImageViewSpec: ImageViewSpec

    val connectionsArtistImageViewSpec: ImageViewSpec

    val plusHeroImageViewSpec: ImageViewSpec
    val shadowStatusBarImageViewSpec: ImageViewSpec

    val searchShadowImageViewSpec: ImageViewSpec

    val profileCuratorImageViewSpec: ImageViewSpec

    val folderPreviewWallpaperFeedImageViewSpec: ImageViewSpec
}