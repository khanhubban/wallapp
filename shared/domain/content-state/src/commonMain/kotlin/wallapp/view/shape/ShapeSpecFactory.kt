package wallapp.view.shape

import wallapp.pixel.shape.ShapeSpec

interface ShapeSpecFactory {

    val defaultShapeSpec: ShapeSpec

    val profileImageShapeSpec: ShapeSpec

    val artistOnboardingShapeSpec: ShapeSpec

    val buttonShapeSpecCircular: ShapeSpec
    val buttonShapeSpecDefault: ShapeSpec

    val modalBottomSheetShapeSpec: ShapeSpec

    val collectionLaneLayerShapeSpecs: List<ShapeSpec>
    val collectionWideLayerShapeSpecs: List<ShapeSpec>
    val collectionWideFooterShapeSpec: ShapeSpec

    val exploreToolbarShapeSpec: ShapeSpec

    val feedContentPreviewColumnShapeSpec: ShapeSpec
    val feedContentPreviewWideShapeSpec: ShapeSpec
    val folderPreviewShapeSpec: ShapeSpec
    val folderPreviewShadowShapeSpec: ShapeSpec

    val searchResultsHeaderShapeSpec: ShapeSpec
    val searchColorBorderShapeSpec: ShapeSpec
    val searchColorBackgroundShapeSpec: ShapeSpec
    val searchContentCategoryShapeSpec: ShapeSpec
    val searchInputBarShapeSpec: ShapeSpec
    val searchInputDropShadowShape: ShapeSpec
    val searchSelectionButtonShapeSpec: ShapeSpec

    val wallpaperPreviewShapeSpecArtistSelectionBackground: ShapeSpec
    val wallpaperPreviewShapeSpecCarouselBackground: ShapeSpec
    val wallpaperPreviewShapeSpecCarouselHighlight: ShapeSpec
    val wallpaperPreviewShapeSpecFeedNonSingle: ShapeSpec
    val wallpaperPreviewShapeSpecFeedSingle: ShapeSpec
    val wallpaperPreviewShapeSpecStory: ShapeSpec
    val wallpaperPreviewShapeSpecUnlockWallpaper: ShapeSpec
    val wallpaperPreviewShapeSpecWallpaperShowcase: ShapeSpec
}