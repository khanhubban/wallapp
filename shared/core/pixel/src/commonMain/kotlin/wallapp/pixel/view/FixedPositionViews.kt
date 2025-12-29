package wallapp.pixel.view

import androidx.compose.runtime.Immutable

@Immutable
data class FixedPositionViews(
    val feedPosition: Int,
    val views: List<View>,
)
