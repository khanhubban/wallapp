package wallapp.content.state.wallpaper

import androidx.compose.runtime.Immutable
import wallapp.pixel.view.ViewState

@Immutable
data class WallpaperDetailViewState(
    val viewSpec: WallpaperDetailViewSpec,
    val detailItems: List<WallpaperDetailItem>?,
) : ViewState {

    companion object {
        val Preset = WallpaperDetailViewState(
            viewSpec = WallpaperDetailViewSpec.Preset,
            detailItems = null,
        )

        fun from(
            categories: WallpaperDetailItem?,
            dimensionsHd: WallpaperDetailItem?,
            dimensionsSd: WallpaperDetailItem?,
            copyright: WallpaperDetailItem?,
        ): List<WallpaperDetailItem>? {
            return listOfNotNull(
                categories,
                dimensionsHd,
                dimensionsSd,
                copyright,
            ).ifEmpty { null }
        }
    }
}