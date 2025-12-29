package wallapp.pixel.feed

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import wallapp.pixel.input.NestedScrollController

val LocalFeedToolbarNestedScrollController: ProvidableCompositionLocal<NestedScrollController?> =
    compositionLocalOf { null }

val LocalExploreFeedToolbarNestedScrollController: ProvidableCompositionLocal<NestedScrollController?> =
    compositionLocalOf { null }