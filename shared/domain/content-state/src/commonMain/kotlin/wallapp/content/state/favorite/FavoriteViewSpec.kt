package wallapp.content.state.favorite

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import wallapp.pixel.view.ViewSpec

@Immutable
data class FavoriteViewSpec(
    val size: Dp,
) : ViewSpec {

    companion object {
        val Preset = FavoriteViewSpec(
            size = 40.dp,
        )
    }
}
