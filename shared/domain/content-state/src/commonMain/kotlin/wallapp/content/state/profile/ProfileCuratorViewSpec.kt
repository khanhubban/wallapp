package wallapp.content.state.profile

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import wallapp.pixel.feed.FeedViewViewSpec
import wallapp.pixel.shape.ShapeSpec

@Immutable
data class ProfileCuratorViewSpec(
    val width: Dp,
    val height: Dp,
    val profileShadowImageSize: Dp,
    val containerShapeSpec: ShapeSpec,
) : FeedViewViewSpec
