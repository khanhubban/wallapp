package wallapp.pixel.input

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity

/**
 * Mirrors [androidx.compose.ui.input.nestedscroll.NestedScrollConnection] but allows for
 * delegation of events to a parent connection.
 *
 * Chief difference is these functions do no return a value, thus do not consume input.
 */
interface NestedScrollController {

    val currentYOffsetPx: Float

    fun hide()

    fun show()

    fun onPreScroll(available: Offset, source: NestedScrollSource) { }

    fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource) { }

    suspend fun onPreFling(available: Velocity) { }

    suspend fun onPostFling(consumed: Velocity, available: Velocity) { }
}
