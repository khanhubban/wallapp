package wallapp.content.state.collection

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import wallapp.pixel.feed.FeedViewViewSpec
import wallapp.pixel.util.DpOptional

@Immutable
data class CollectionPreviewViewSpec(
    val width: Dp,
    val height: Dp,
    val footerHeight: DpOptional?,
    override val useMaxItemSpan: Boolean? = true,
) : FeedViewViewSpec {

    companion object {

        val Preset = CollectionPreviewViewSpec(
            width = 240.dp,
            height = 240.dp,
            footerHeight = null,
            useMaxItemSpan = true,
        )
    }
}