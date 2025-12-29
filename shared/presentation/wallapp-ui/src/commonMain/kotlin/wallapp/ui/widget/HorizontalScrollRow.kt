package wallapp.ui.widget

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import wallapp.content.state.widget.HorizontalScrollRowViewState
import wallapp.pixel.render.Render
import wallapp.pixel.view.View

@Composable
fun HorizontalScrollRow(
    render: Render,
    viewState: HorizontalScrollRowViewState,
    modifier: Modifier,
) {
    val views = viewState.views

    Box(
        modifier = modifier,//.height(height),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            views.forEach { view ->
                view.viewSpec
                val viewModifier = Modifier//.width(300.dp)
                View(render, view = view, modifier = viewModifier)
            }
        }
    }
}