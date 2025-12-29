package wallapp.pixel.selection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import wallapp.pixel.compose.conditional
import wallapp.pixel.render.Render

@Composable
fun SelectionGroup(
    render: Render,
    viewState: SelectionGroupViewState,
    modifier: Modifier = Modifier,
) {
    when (viewState.viewSpec) {
        is SelectionGroupViewSpec.Row -> {
            SelectionGroupRow(render, viewState, modifier)
        }

        is SelectionGroupViewSpec.Grid -> {
            SelectionGroupGrid(render, viewState, modifier)
        }
    }
}

@Composable
fun SelectionGroupRow(
    render: Render,
    viewState: SelectionGroupViewState,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
    ) {
        viewState.selections.forEach { selection ->
            Selection(render, selection, modifier)
        }
    }
}

@Composable
fun SelectionGroupGrid(
    render: Render,
    viewState: SelectionGroupViewState,
    modifier: Modifier = Modifier,
) {
    val gridType: SelectionGroupViewSpec.Grid =  viewState.viewSpec as SelectionGroupViewSpec.Grid
    val items = viewState.selections
    val itemWidth = viewState.viewSpec.itemWidth
    val itemHeight = viewState.viewSpec.itemHeight
    val containerPaddingValues = gridType.containerPaddingValues
    val horizontalItemSpacing = gridType.horizontalItemSpacing
    val verticalItemSpacing = gridType.verticalItemSpacing

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = itemWidth),
        modifier = modifier.conditional(containerPaddingValues != null) {
            padding(containerPaddingValues!!)
        },
        horizontalArrangement = Arrangement.spacedBy(horizontalItemSpacing),
        verticalArrangement = Arrangement.spacedBy(verticalItemSpacing),
    ) {
        items.forEach { selection ->
            item {
                Selection(
                    render = render,
                    viewState = selection,
                    modifier = Modifier
                        .width(itemWidth)
                        .height(itemHeight),
                )
            }
        }
    }
}
