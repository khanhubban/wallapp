package wallapp.content.state.favorite

import androidx.compose.runtime.Immutable
import wallapp.image.Image
import wallapp.pixel.view.ViewEventHandler
import wallapp.pixel.view.ViewState
import wallapp.theme.ColorToken

@Immutable
data class FavoriteViewState(
    val id: String,
    val viewSpec: FavoriteViewSpec,
    val isFavorite: Boolean,
    val selectedImage: Image,
    val unselectedImage: Image,
    val onClick: ViewEventHandler,
    val favoriteContentDescription: String,
    val selectedImageAnimated: Image? = null,
    val unselectedImageAnimated: Image? = null,
    val tintColor: ColorToken? = null,
) : ViewState {

    companion object {
        val Preset = FavoriteViewState(
            id = "favorite",
            viewSpec = FavoriteViewSpec.Preset,
            isFavorite = false,
            selectedImage = Image.Preset,
            unselectedImage = Image.Preset,
            onClick = ViewEventHandler.NoOp,
            favoriteContentDescription = "favorite off",
        )
    }
}
