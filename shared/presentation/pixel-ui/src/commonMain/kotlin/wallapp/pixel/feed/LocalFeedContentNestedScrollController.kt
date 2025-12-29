package wallapp.pixel.feed

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import wallapp.pixel.input.NestedScrollConsumingFeedController

val LocalFeedContentNestedScrollController: ProvidableCompositionLocal<NestedScrollConsumingFeedController?> =
    compositionLocalOf { null }

