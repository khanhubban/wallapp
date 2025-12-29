package wallapp.content.state.artist

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import wallapp.pixel.view.ViewSpec


@Immutable
data class ArtistViewSpec(
    val paddingDefault: Dp = 16.dp,
    val toolbarViewSpec: ArtistToolbarViewSpec,
    val profileImageSize: Dp = 100.dp,
) : ViewSpec