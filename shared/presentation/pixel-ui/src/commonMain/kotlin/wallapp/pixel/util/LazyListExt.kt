package wallapp.pixel.util

import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState

val LazyStaggeredGridState.isScrollable: Boolean
    get() = !(!canScrollForward && !canScrollBackward)
