package wallapp.pixel.input

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity

interface NestedScrollConsumingController {
    val currentYOffsetPx: Float

    fun onPreScroll(available: Offset, source: NestedScrollSource): Offset

    fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset

    suspend fun onPreFling(available: Velocity) { }

    suspend fun onPostFling(consumed: Velocity, available: Velocity) { }
}