package wallapp.pixel.spacer

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
fun Spacer(
    viewState: SpacerViewState,
    modifier: Modifier,
) {
    val width = viewState.width?.dp
    val height = viewState.height?.dp
    Spacer(modifier, width = width, height = height)
}


@Composable
fun Spacer(
    modifier: Modifier = Modifier,
    width: Dp? = null,
    height: Dp? = null,
) {
    if (width != null && height != null) {
        Spacer(
            modifier = modifier
                .width(width)
                .height(height),
        )
    } else if (width != null) {
        Spacer(
            modifier = modifier
                .width(width)
                .fillMaxHeight(),
        )
    } else if (height != null) {
        Spacer(
            modifier = modifier
                .height(height)
                .fillMaxWidth(),
        )
    }
}