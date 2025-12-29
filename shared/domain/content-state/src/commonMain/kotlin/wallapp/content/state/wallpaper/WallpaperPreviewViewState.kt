package wallapp.content.state.wallpaper

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.dp
import wallapp.content.state.favorite.FavoriteViewState
import wallapp.content.state.upgrade.plus.indicator.PlusIndicatorViewState
import wallapp.image.Image
import wallapp.pixel.image.ImageViewSpec
import wallapp.pixel.image.ImageViewState
import wallapp.pixel.text.Text
import wallapp.pixel.text.Text.Companion.presetText
import wallapp.pixel.view.ViewEventHandler
import wallapp.pixel.view.ViewId
import wallapp.pixel.view.ViewState
import wallapp.string.quote

@Immutable
data class WallpaperPreviewViewState(
    val viewSpec: WallpaperPreviewViewSpec,
    override val viewId: ViewId,
    val imageViewState: ImageViewState,
    val title: Text?,
    val subtitle: Text?,
    val scrimImage: Image,
    val favorite: FavoriteViewState,
    val plusIndicator: PlusIndicatorViewState?,
    val isSingle: Boolean,
    val onClick: ViewEventHandler,
): ViewState {

    init {
        require(IdSuffix in viewId.id) { "id must contain ${IdSuffix.quote()} - use `createId()`" }
    }

    companion object {
        // Append suffix to avoid id collisions with other views
        const val IdSuffix = "-wp"

        val Preset = WallpaperPreviewViewState(
            viewSpec = WallpaperPreviewViewSpec.Preset,
            viewId = ViewId("preset~wallpaper$IdSuffix"),
            imageViewState = ImageViewState(
                image = Image.Preset,
                viewSpec = ImageViewSpec(size = 100.dp),
                imageSize = null,
            ),
            title = "A Wallpaper".presetText,
            subtitle = null,
            scrimImage = Image.Preset,
            favorite = FavoriteViewState.Preset,
            isSingle = true,
            plusIndicator = null,
            onClick = ViewEventHandler.NoOp,
        )
    }
}