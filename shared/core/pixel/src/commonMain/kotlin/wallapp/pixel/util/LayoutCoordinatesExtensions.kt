package wallapp.pixel.util

import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionInWindow
import wallapp.math.geometry.Rect


val LayoutCoordinates.positionInWindowRect: Rect
    get() {
        val windowPosition = positionInWindow()
        val size = size
        return Rect(
            left = windowPosition.x,
            top = windowPosition.y,
            right = windowPosition.x + size.width.toFloat(),
            bottom = windowPosition.y + size.height.toFloat(),
        )
    }