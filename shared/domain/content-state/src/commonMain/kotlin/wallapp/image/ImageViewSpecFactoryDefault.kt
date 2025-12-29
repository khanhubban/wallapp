package wallapp.image

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import wallapp.pixel.compose.FullWidthDp
import wallapp.pixel.image.ImageViewSpec
import wallapp.pixel.view.ViewContentScale
import wallapp.view.ViewAlignmentFactory
import wallapp.view.ViewSpecArbitrator
import wallapp.view.shape.ShapeSpecFactory

class ImageViewSpecFactoryDefault(
    private val viewSpecArbitrator: ViewSpecArbitrator,
    private val viewAlignmentFactory: ViewAlignmentFactory,
    private val shapeSpecFactory: ShapeSpecFactory,
) : ImageViewSpecFactory {

    private val deviceWidth: Dp
        get() = viewSpecArbitrator.windowWidth
    private val deviceHeight: Dp
        get() = viewSpecArbitrator.windowHeight

    private fun ProfileImageViewSpec(size: Dp): ImageViewSpec {
        return ImageViewSpec(
            size = size,
            shapeSpec = shapeSpecFactory.profileImageShapeSpec,
        )
    }

    override val wallpaperFeedSingleImageViewSpec: ImageViewSpec
        get() = ImageViewSpec(
            width = viewSpecArbitrator.feedItemSingleSpanWidth,
            height = viewSpecArbitrator.feedItemSingleSpanHeight,
            shapeSpec = null,
            alignment = viewAlignmentFactory.default,
        )

    override val wallpaperFeedTrackImageViewSpec: ImageViewSpec
        get() = ImageViewSpec(
            width = viewSpecArbitrator.feedItemSingleSpanWidth,
            height = viewSpecArbitrator.feedItemSingleSpanWidth,
            shapeSpec = null,
            alignment = viewAlignmentFactory.default,
        )

    override val wallpaperShowcaseImageViewSpec: ImageViewSpec
        get() = ImageViewSpec(
            width = viewSpecArbitrator.wallpaperPreviewWidthMax,
            height = viewSpecArbitrator.wallpaperPreviewHeightWallpaperShowcase,
            shapeSpec = null,
        )

    override val carouselHighlightImageViewSpec: ImageViewSpec
        get() = ImageViewSpec(
            width = viewSpecArbitrator.carouselHighlightImageWidth,
            height = viewSpecArbitrator.carouselHighlightImageHeight,
            shapeSpec = shapeSpecFactory.wallpaperPreviewShapeSpecCarouselHighlight,
            contentScale = ViewContentScale.FillWidth,
        )

    override val artistSelectionBackgroundImageViewSpec: ImageViewSpec
        get() = ImageViewSpec(
            width = viewSpecArbitrator.artistOnboardingWallpaperBackgroundWidth,
            height = viewSpecArbitrator.artistOnboardingWallpaperBackgroundHeight,//
            shapeSpec = shapeSpecFactory.wallpaperPreviewShapeSpecArtistSelectionBackground,
        )

    override val artistProfileImageMediumImageViewSpec: ImageViewSpec
        get() = ProfileImageViewSpec(size = viewSpecArbitrator.artistProfileImageMediumSize)

    override val artistProfileImageSmallImageViewSpec: ImageViewSpec
        get() = ProfileImageViewSpec(56.dp)

    override val wallpaperArtistProfileImageViewSpec: ImageViewSpec
        get() = artistProfileImageSmallImageViewSpec

    override val connectionsArtistImageViewSpec: ImageViewSpec
        get() = artistProfileImageSmallImageViewSpec

    override val fullScreenImageViewSpec: ImageViewSpec
        get() = ImageViewSpec(
            width = deviceWidth,
            height = deviceHeight,
            shapeSpec = shapeSpecFactory.wallpaperPreviewShapeSpecCarouselBackground,
        )

    override val plusHeroImageViewSpec: ImageViewSpec
        get() = ImageViewSpec(
            width = viewSpecArbitrator.plusHeroImageWidth,
            height = viewSpecArbitrator.plusHeroImageHeight,
            shapeSpec = null,
            contentScale = ViewContentScale.FillHeight,
        )
    override val shadowStatusBarImageViewSpec: ImageViewSpec
        get() = ImageViewSpec(
            width = viewSpecArbitrator.windowWidth,
            height = viewSpecArbitrator.statusBarHeight,
            contentScale = ViewContentScale.FillBounds,
            shapeSpec = null,
        )

    private val collectionPreviewLayerViewSpecFull0: ImageViewSpec
        get() = ImageViewSpec(
            width = viewSpecArbitrator.collectionPreviewFullLayer0Width,
            height = viewSpecArbitrator.collectionPreviewFullLayer0Height,
            shapeSpec = shapeSpecFactory.collectionWideLayerShapeSpecs[0],
        )
    private val collectionPreviewLayerViewSpecFull1: ImageViewSpec
        get() = ImageViewSpec(
            width = viewSpecArbitrator.collectionPreviewFullLayer1Width,
            height = viewSpecArbitrator.collectionPreviewFullLayer1Height,
            shapeSpec = shapeSpecFactory.collectionWideLayerShapeSpecs[1],
        )
    private val collectionPreviewLayerViewSpecFull2: ImageViewSpec
        get() = ImageViewSpec(
            width = viewSpecArbitrator.collectionPreviewFullLayer2Width,
            height = viewSpecArbitrator.collectionPreviewFullLayer2Height,
            shapeSpec = shapeSpecFactory.collectionWideLayerShapeSpecs[2],
        )

    override fun collectionPreviewFullImageViewSpec(index: Int): ImageViewSpec {
        return when (index) {
            0 -> collectionPreviewLayerViewSpecFull0
            1 -> collectionPreviewLayerViewSpecFull1
            2 -> collectionPreviewLayerViewSpecFull2
            else -> throw IllegalArgumentException("Invalid index: $index")
        }
    }

    override fun collectionPreviewFullImageViewSpecs(): List<ImageViewSpec> =
        listOf(
            collectionPreviewFullImageViewSpec(index = 0),
            collectionPreviewFullImageViewSpec(index = 1),
            collectionPreviewFullImageViewSpec(index = 2),
        )


    private val collectionPreviewLayerViewSpecLane0: ImageViewSpec
        get() = ImageViewSpec(
            width = viewSpecArbitrator.collectionPreviewSmallLayer0Width,
            height = viewSpecArbitrator.collectionPreviewSmallLayer0Height,
            shapeSpec = shapeSpecFactory.collectionLaneLayerShapeSpecs[0],
        )
    private val collectionPreviewLayerViewSpecLane1: ImageViewSpec
        get() = ImageViewSpec(
            width = viewSpecArbitrator.collectionPreviewSmallLayer1Width,
            height = viewSpecArbitrator.collectionPreviewSmallLayer1Height,
            shapeSpec = shapeSpecFactory.collectionLaneLayerShapeSpecs[1],
        )
    private val collectionPreviewLayerViewSpecLane2: ImageViewSpec
        get() = ImageViewSpec(
            width = viewSpecArbitrator.collectionPreviewSmallLayer2Width,
            height = viewSpecArbitrator.collectionPreviewSmallLayer2Height,
            shapeSpec = shapeSpecFactory.collectionLaneLayerShapeSpecs[2],
        )

    override fun collectionPreviewSmallImageViewSpec(index: Int): ImageViewSpec {
        return when (index) {
            0 -> collectionPreviewLayerViewSpecLane0
            1 -> collectionPreviewLayerViewSpecLane1
            2 -> collectionPreviewLayerViewSpecLane2
            else -> throw IllegalArgumentException("Invalid index: $index")
        }
    }

    override fun collectionPreviewSmallImageViewSpecs(): List<ImageViewSpec> {
        return listOf(
            collectionPreviewSmallImageViewSpec(index = 0),
            collectionPreviewSmallImageViewSpec(index = 1),
            collectionPreviewSmallImageViewSpec(index = 2),
        )
    }

    override val accountProfileImageViewSpec: ImageViewSpec
        get() = ProfileImageViewSpec(size = 84.dp)

    override val accountOverviewProfileImageViewSpec: ImageViewSpec
        get() = ImageViewSpec(size = 88.dp)

    override val artistPreviewProfileImageViewSpecOnboarding: ImageViewSpec
        get() = artistProfileImageMediumImageViewSpec

    override val artistToolbarProfileImageViewSpec: ImageViewSpec
        get() = artistProfileImageMediumImageViewSpec
    override val artistsProfileImageViewSpec: ImageViewSpec
        get() = artistProfileImageSmallImageViewSpec

    override val collectionToolbarArtistProfileImageViewSpec: ImageViewSpec
        get() = artistProfileImageMediumImageViewSpec

    override val homeTopBarProfileImageViewSpec: ImageViewSpec
        get() = ImageViewSpec(size = viewSpecArbitrator.profileImageHeightToolbar)
    override val homeArtistPreviewImageViewSpec: ImageViewSpec
        get() = artistProfileImageSmallImageViewSpec

    override val searchShadowImageViewSpec: ImageViewSpec
        get() = ImageViewSpec(
            width = FullWidthDp, // Need to handle width at UI level as windowWidth is not ready when this is first called
            height = 100.dp,
            shapeSpec = shapeSpecFactory.searchInputDropShadowShape,
        )

    override val profileCuratorImageViewSpec: ImageViewSpec
        get() = artistPreviewProfileImageViewSpecOnboarding

    override val folderPreviewWallpaperFeedImageViewSpec: ImageViewSpec
        get() = ImageViewSpec(
            width = viewSpecArbitrator.folderPreviewWallpaperFeedItemWidth,
            height = viewSpecArbitrator.folderPreviewWallpaperFeedItemHeight,
            shapeSpec = null,
            alignment = viewAlignmentFactory.default,
        )
}