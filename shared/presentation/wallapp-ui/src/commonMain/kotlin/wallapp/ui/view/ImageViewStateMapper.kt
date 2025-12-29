package wallapp.ui.view

import wallapp.content.state.wallpaper.WallpaperPreviewViewState
import wallapp.pixel.image.ImageViewState
import wallapp.pixel.view.ViewState

object ImageViewStateMapper {

    fun map(viewState: ViewState): ImageViewState? {
        return when (viewState) {
            is WallpaperPreviewViewState -> {
                viewState.imageViewState
            }

            else -> {
                null
            }
        }
    }

}
