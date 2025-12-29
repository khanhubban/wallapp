package wallapp.ui.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import wallapp.content.state.widget.WidgetViewState
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.render.Render

@Composable
fun Widget(
    render: Render,
    viewState: WidgetViewState,
    modifier: Modifier = Modifier,
) {
    MenuItem(render, menuItem = viewState.menuItem, modifier = modifier)
}