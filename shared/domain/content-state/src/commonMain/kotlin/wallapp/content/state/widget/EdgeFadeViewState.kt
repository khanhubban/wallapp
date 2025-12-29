package wallapp.content.state.widget

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import wallapp.graphics.Color
import wallapp.image.Image
import wallapp.pixel.util.DpOptional
import wallapp.theme.ColorToken

@Immutable
data class EdgeFadeViewState(
    val image: Image,
    val viewSpec: EdgeFadeViewSpec,
)

@Immutable
data class EdgeFadeViewSpec(
    val startPadding: Dp,
    val endPadding: Dp,
    val startWidth: Dp,
    val endWidth: Dp,
    val tintColorToken: ColorToken,
    val height: DpOptional? = null,
    val endOffsetX: Dp = 0.dp,
) {
    companion object {
        val Default = EdgeFadeViewSpec(
            startPadding = 0.dp,
            endPadding = 0.dp,
            startWidth = 0.dp,
            endWidth = 0.dp,
            tintColorToken = ColorToken.Custom(Color.Unspecified),
        )
    }
}