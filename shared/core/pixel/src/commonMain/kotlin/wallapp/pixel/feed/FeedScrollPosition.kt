package wallapp.pixel.feed

import co.touchlab.skie.configuration.annotations.EnumInterop

@EnumInterop.Enabled
enum class FeedScrollPosition {
    Top,
    Bottom,
    Middle,
    Unknown
}

typealias FeedScrollPositionUpdateSink = (FeedScrollPosition) -> Unit