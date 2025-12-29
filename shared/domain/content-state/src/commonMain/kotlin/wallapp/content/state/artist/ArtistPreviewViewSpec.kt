package wallapp.content.state.artist

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import wallapp.pixel.feed.FeedViewViewSpec

@Immutable
data class ArtistPreviewViewSpec(
    val profileRowHeight: Dp = 72.dp,
    val imagePreviewHeight: Dp = 120.dp,
    val paddingDefault: Dp = 16.dp,
    override val useMaxItemSpan: Boolean = false,
) : FeedViewViewSpec {

    val height: Dp
        get() = profileRowHeight + imagePreviewHeight

    companion object {
        val Default = ArtistPreviewViewSpec()
        val MaxSpan = ArtistPreviewViewSpec(
            profileRowHeight = 80.dp,
            imagePreviewHeight = 180.dp,
            useMaxItemSpan = true,
        )
    }

}