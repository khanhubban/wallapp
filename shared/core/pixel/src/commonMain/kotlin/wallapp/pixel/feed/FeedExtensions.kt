package wallapp.pixel.feed

import wallapp.pixel.view.View

val View.isMaxItemSpan: Boolean
    get() = this.viewSpec != null
            && this.viewSpec is FeedViewViewSpec
            && (this.viewSpec as FeedViewViewSpec).useMaxItemSpan == true

val View.useZeroFeedPadding: Boolean
    get() {
        if (this.viewSpec != null
            && this.viewSpec is FeedViewViewSpec
            && (this.viewSpec as FeedViewViewSpec).useZeroFeedPadding == true
        ) {
            return true
        }
        return false
    }