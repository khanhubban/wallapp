package wallapp.pixel.util

import androidx.compose.ui.layout.LayoutCoordinates

fun interface GloballyPositionedListener {

    fun onPositioned(layoutCoordinates: LayoutCoordinates)
}