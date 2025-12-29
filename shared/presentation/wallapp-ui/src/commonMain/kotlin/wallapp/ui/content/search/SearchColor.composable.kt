@file:Suppress("UNUSED_PARAMETER")

package wallapp.ui.content.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import wallapp.content.state.search.SearchColorViewState
import wallapp.graphics.composeColor
import wallapp.pixel.clickable.clickable
import wallapp.pixel.compose.conditional
import wallapp.pixel.render.Render

@Composable
fun SearchColor(
    render: Render,
    viewState: SearchColorViewState,
    modifier: Modifier = Modifier,
    backgroundSize: Dp,
    borderShape: Shape = RectangleShape,
    backgroundShape: Shape = RectangleShape,
) {
    val eventSink = viewState.eventSink
    val event = viewState.event

    val isSelected = viewState.isSelected
    val color = viewState.color.composeColor
    val canShowBorder = viewState.canShowBorder

    val borderColor = if (isSelected) {
        MaterialTheme.colorScheme.onBackground
    } else {
        null
    }
    val borderColorInside = if (canShowBorder) {
        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.47f)
    } else {
        null
    }

    Box(
        modifier = modifier.clip(shape = borderShape)
            .conditional(borderColor != null) {
                this.border(width = 2.dp, color = borderColor!!, shape = borderShape)
            }
            .clickable(render) { eventSink(event) },
    ) {
        Box(
            modifier = Modifier.size(backgroundSize)
                .clip(shape = backgroundShape)
                .align(Alignment.Center)
                .conditional(borderColorInside != null) {
                    this.border(width = 2.dp, color = borderColorInside!!, shape = backgroundShape)
                }
                .background(color = color)
        )
    }

}