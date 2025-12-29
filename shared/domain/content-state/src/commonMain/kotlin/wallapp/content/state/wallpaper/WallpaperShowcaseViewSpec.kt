package wallapp.content.state.wallpaper

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import wallapp.pixel.shape.ShapeSpec
import wallapp.pixel.view.ViewSpec
import wallapp.view.ViewSpecDefaults

@Immutable
data class WallpaperShowcaseViewSpec(
    val previewImagePageSpacing: Dp = ViewSpecDefaults.PaddingLarge / 2,
    val topControlButtonsVerticalOffset: Dp,
    val bottomPadding: Dp,
    val horizontalPadding: Dp = ViewSpecDefaults.PaddingLarge,
    val wallpaperPreviewShapeSpec: ShapeSpec,
    val indicatorSpacing: Dp = 4.dp,
    val indicatorSize: Dp = ViewSpecDefaults.PaddingMedium / 2,
    val indicatorContainerHeight: Dp,
    val maxTitleSpacing: Dp = ViewSpecDefaults.PaddingDefault + ViewSpecDefaults.PaddingSmall,
    val maxArtistTopSpacing: Dp = ViewSpecDefaults.PaddingDefault + ViewSpecDefaults.PaddingSmall/2,
    val minArtistTopSpacing: Dp = ViewSpecDefaults.PaddingSmall/2,
    val maxArtistBottomSpacing: Dp = ViewSpecDefaults.PaddingDefault,
    val minArtistBottomSpacing: Dp = ViewSpecDefaults.PaddingSmall/4,
) : ViewSpec {

        companion object {
            val Preset = WallpaperShowcaseViewSpec(
                topControlButtonsVerticalOffset = 0.dp,
                bottomPadding = ViewSpecDefaults.PaddingLarge,
                wallpaperPreviewShapeSpec = ShapeSpec.Preset,
                indicatorContainerHeight = 20.dp,
            )
        }
}
