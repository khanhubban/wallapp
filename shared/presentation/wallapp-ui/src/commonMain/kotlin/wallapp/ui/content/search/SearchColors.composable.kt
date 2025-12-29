package wallapp.ui.content.search

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import wallapp.content.state.search.SearchColorsViewState
import wallapp.pixel.render.Render
import wallapp.pixel.render.shapeMapperComposable

@Composable
fun SearchColors(
    render: Render,
    viewState: SearchColorsViewState,
    modifier: Modifier = Modifier,
) {
    val itemSize = viewState.itemSize
    val colors = viewState.colors

    Row(
        modifier = modifier
            .height(itemSize)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        colors.forEachIndexed { index, colorViewState ->
            SearchColor(
                render = render,
                viewState = colorViewState,
                modifier = Modifier.size(itemSize),
                borderShape = render.shapeMapperComposable.map(viewState.borderShape)!!,
                backgroundSize = viewState.insideItemSize,
                backgroundShape = render.shapeMapperComposable.map(viewState.backgroundShape)!!,
            )
            if (index < colors.size - 1) {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}
