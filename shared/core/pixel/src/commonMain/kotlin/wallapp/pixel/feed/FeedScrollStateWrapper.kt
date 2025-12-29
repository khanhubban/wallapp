package wallapp.pixel.feed

data class FeedScrollStateWrapper(
    val lastScrollState: FeedScrollState?,
    val lastScrollStateUpdateSink: LastScrollStateUpdateSink?,
)