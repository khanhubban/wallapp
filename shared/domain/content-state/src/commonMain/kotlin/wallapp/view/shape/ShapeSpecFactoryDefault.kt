package wallapp.view.shape

import wallapp.pixel.shape.ShapeSize
import wallapp.pixel.shape.ShapeSpec
import wallapp.pixel.shape.ShapeStyle

class ShapeSpecFactoryDefault : ShapeSpecFactory {

    private val rectangleShapeSpec: ShapeSpec
        get() = ShapeSpec(ShapeStyle.Rectangle, ShapeSize.Small)

    private val circularShapeSpec: ShapeSpec
        get() = ShapeSpec(ShapeStyle.Circle, ShapeSize.Small)

    override val defaultShapeSpec: ShapeSpec
        get() = ShapeSpec(ShapeStyle.RoundedCorners, ShapeSize.Small)
    private val roundedCornersBottomShapeSpec: ShapeSpec
        get() = ShapeSpec(ShapeStyle.RoundedCornersBottom, ShapeSize.Small)

    override val modalBottomSheetShapeSpec: ShapeSpec
        get() = ShapeSpec(ShapeStyle.RoundedCornersTop, ShapeSize.ExtraLarge)

    override val profileImageShapeSpec: ShapeSpec
        get() = circularShapeSpec

    override val artistOnboardingShapeSpec: ShapeSpec
        get() = feedContentPreviewColumnShapeSpec

    override val buttonShapeSpecCircular: ShapeSpec
        get() = circularShapeSpec
    override val buttonShapeSpecDefault: ShapeSpec
        get() = defaultShapeSpec

    private val collectionLaneLayer1ShapeSpec: ShapeSpec
        get() = defaultShapeSpec
    private val collectionLaneLayer2ShapeSpec: ShapeSpec
        get() = roundedCornersBottomShapeSpec
    private val collectionLaneLayer3ShapeSpec: ShapeSpec
        get() = roundedCornersBottomShapeSpec
    override val collectionLaneLayerShapeSpecs: List<ShapeSpec>
        get() = listOf(
            collectionLaneLayer1ShapeSpec,
            collectionLaneLayer2ShapeSpec,
            collectionLaneLayer3ShapeSpec,
        )

    private val collectionWideLayer1ShapeSpec: ShapeSpec
        get() = defaultShapeSpec
    private val collectionWideLayer2ShapeSpec: ShapeSpec
        get() = roundedCornersBottomShapeSpec
    private val collectionWideLayer3ShapeSpec: ShapeSpec
        get() = roundedCornersBottomShapeSpec
    override val collectionWideLayerShapeSpecs: List<ShapeSpec>
        get() = listOf(
            collectionWideLayer1ShapeSpec,
            collectionWideLayer2ShapeSpec,
            collectionWideLayer3ShapeSpec,
        )

    override val collectionWideFooterShapeSpec: ShapeSpec
        get() = roundedCornersBottomShapeSpec

    override val exploreToolbarShapeSpec: ShapeSpec
        get() = ShapeSpec(ShapeStyle.RoundedCornersTop, ShapeSize.Medium)

    override val feedContentPreviewColumnShapeSpec: ShapeSpec
        get() = defaultShapeSpec

    override val feedContentPreviewWideShapeSpec: ShapeSpec
        get() = defaultShapeSpec
    override val folderPreviewShapeSpec: ShapeSpec
        get() = defaultShapeSpec
    override val folderPreviewShadowShapeSpec: ShapeSpec
        get() = ShapeSpec(ShapeStyle.RoundedCornersTop, ShapeSize.Small)

    override val searchResultsHeaderShapeSpec: ShapeSpec
        get() = searchInputBarShapeSpec
    override val searchColorBorderShapeSpec: ShapeSpec
        get() = ShapeSpec(ShapeStyle.RoundedCorners, ShapeSize.Medium)
    override val searchColorBackgroundShapeSpec: ShapeSpec
        get() = ShapeSpec(ShapeStyle.RoundedCorners, ShapeSize.Small)
    override val searchContentCategoryShapeSpec: ShapeSpec
        get() = ShapeSpec(ShapeStyle.Circle, ShapeSize.Small)
    override val searchInputBarShapeSpec: ShapeSpec
        get() = ShapeSpec(ShapeStyle.RoundedCorners, ShapeSize.Medium)
    override val searchInputDropShadowShape: ShapeSpec
        get() = ShapeSpec(ShapeStyle.RoundedCorners, ShapeSize.Large)
    override val searchSelectionButtonShapeSpec: ShapeSpec
        get() = ShapeSpec(ShapeStyle.RoundedCorners, ShapeSize.Large)

    override val wallpaperPreviewShapeSpecArtistSelectionBackground: ShapeSpec
        get() = rectangleShapeSpec
    override val wallpaperPreviewShapeSpecCarouselBackground: ShapeSpec
        get() = rectangleShapeSpec
    override val wallpaperPreviewShapeSpecCarouselHighlight: ShapeSpec
        get() = defaultShapeSpec
    override val wallpaperPreviewShapeSpecFeedNonSingle: ShapeSpec
        get() = feedContentPreviewColumnShapeSpec
    override val wallpaperPreviewShapeSpecFeedSingle: ShapeSpec
        get() = feedContentPreviewColumnShapeSpec
    override val wallpaperPreviewShapeSpecStory: ShapeSpec
        get() = rectangleShapeSpec
    override val wallpaperPreviewShapeSpecUnlockWallpaper: ShapeSpec
        get() = defaultShapeSpec
    override val wallpaperPreviewShapeSpecWallpaperShowcase: ShapeSpec
        get() = ShapeSpec(ShapeStyle.RoundedCornersBottom, ShapeSize.Small)
}