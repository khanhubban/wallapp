package wallapp.ui.content.image

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import wallapp.pixel.render.Render

@Composable
fun ImageStack(
    render: Render,
    items: List<ImageGridItem>,
    modifier: Modifier = Modifier,
) {
    val layerEdgeWeight = .12f
    val itemsReversed = items.reversed()

    Box(modifier = modifier) {
        itemsReversed.forEachIndexed { index, image ->
            val adjustedIndex = items.size - 1 - index
            val adjustedWeight = layerEdgeWeight * adjustedIndex
            val offsetWeight = layerEdgeWeight * index
            val elevation = 4.dp * (adjustedIndex+1)

            Column {
                if (adjustedWeight > 0f) {
                    Spacer(modifier = Modifier.weight(adjustedWeight))
                }

                Row(
                    modifier = Modifier.weight(1f),
                ) {
                    if (adjustedWeight > 0f) {
                        Spacer(modifier = Modifier.weight(adjustedWeight))
                    }

                    ImageContent(
                        render,
                        image,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .shadow(elevation = elevation, shape = RectangleShape),
                    )

                    if (adjustedWeight > 0f) {
                        Spacer(modifier = Modifier.weight(adjustedWeight))
                    }
                }

                if (offsetWeight > 0f) {
                    Spacer(modifier = Modifier.weight(offsetWeight))
                }
            }

        }
    }
}