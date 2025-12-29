package wallapp.content.state.artist

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import wallapp.pixel.animation.AnimatedViewSpec
import wallapp.pixel.view.ViewSpec

@Immutable
data class ArtistToolbarViewSpec(
    val minToolbarHeight: Dp,
    val maxToolbarHeight: Dp,
    val paddingDefault: Dp,
    val profileAnimatedViewSpec: AnimatedViewSpec,
    val separatorViewSpec: AnimatedViewSpec,
    val titleTop: Dp,
    val titleHeight: Dp,
    val socialLinksTop: Dp,
    val socialLinksHeight: Dp,
    val separatorHeight: Dp,
) : ViewSpec
