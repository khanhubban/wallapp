package wallapp.pixel.feed

import androidx.compose.runtime.Immutable

@Immutable
data class FeedScrollState(
    val firstVisibleItemIndex: Int,
    val firstVisibleItemScrollOffset: Int,
)