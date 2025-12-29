package wallapp.unit

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.height as heightFoundation
import androidx.compose.foundation.layout.width as widthFoundation

fun Modifier.heightAx(height: Height?): Modifier {
    if (height == null) return this
    return when (height) {
        is Height.HeightUndefined -> {
            this
        }
        is Height.HeightDp -> {
            this.heightFoundation(height.dp)
        }
        is Height.HeightFillMax -> {
            this.fillMaxHeight(height.fraction)
        }
    }
}

fun Modifier.widthAx(width: Width?): Modifier {
    if (width == null) return this
    return when (width) {
        is Width.WidthUndefined -> {
            this
        }
        is Width.WidthDp -> {
            this.widthFoundation(width.dp)
        }
        is Width.WidthFillMax -> {
            this.fillMaxWidth(width.fraction)
        }
    }
}

fun lerp(
    start: Padding?,
    stop: Padding?,
    amount: Float,
): Padding? {
    if (start == null && stop == null) {
        return null
    }
    require(start != null && stop != null) { "Padding min and max must both be null or non-null" }

    return Padding(
        start = androidx.compose.ui.unit.lerp(start.start, stop.start, amount),
        top = androidx.compose.ui.unit.lerp(start.top, stop.top, amount),
        end = androidx.compose.ui.unit.lerp(start.end, stop.end, amount),
        bottom = androidx.compose.ui.unit.lerp(start.bottom, stop.bottom, amount),
    )
}

