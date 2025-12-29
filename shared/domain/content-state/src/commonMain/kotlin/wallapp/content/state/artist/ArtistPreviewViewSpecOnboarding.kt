package wallapp.content.state.artist

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import wallapp.pixel.feed.FeedViewViewSpec

@Immutable
data class ArtistPreviewViewSpecOnboarding(
    val width: Dp,
    val height: Dp,
    val profileShadowImageSize: Dp,
    val followIconSize: Dp,
) : FeedViewViewSpec
