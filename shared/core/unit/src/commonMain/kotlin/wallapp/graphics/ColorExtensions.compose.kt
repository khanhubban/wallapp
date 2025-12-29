package wallapp.graphics

import androidx.compose.ui.graphics.Color as ComposeColor

val Color.composeColor: ComposeColor
    get() = ComposeColor(red, green, blue, alpha)

val ComposeColor.color: Color
    get() = Color(red, green, blue, alpha)


fun lerp(start: Color, stop: Color, /*@FloatRange(from = 0.0, to = 1.0)*/ fraction: Float): Color {
    return androidx.compose.ui.graphics.lerp(
        start.composeColor, stop.composeColor, fraction
    ).color
}