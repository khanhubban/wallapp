package wallapp.content.state.profile

import androidx.compose.runtime.Immutable
import wallapp.image.Image
import wallapp.pixel.view.ViewState
import wallapp.theme.ColorToken

@Immutable
data class ProfileImageIndicatorViewState(
    val viewSpec: ProfileImageIndicatorViewSpec,
    val image: Image,
    val colorToken: ColorToken,
) : ViewState
