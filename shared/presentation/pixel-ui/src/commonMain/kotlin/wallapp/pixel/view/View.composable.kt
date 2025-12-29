package wallapp.pixel.view

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import wallapp.image.Image
import wallapp.pixel.feed.FeedGrid
import wallapp.pixel.feed.FeedViewState
import wallapp.pixel.image.ImageViewState
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.menu.MenuItemViewState
import wallapp.pixel.render.Render
import wallapp.pixel.selection.Selection
import wallapp.pixel.selection.SelectionGroup
import wallapp.pixel.selection.SelectionGroupViewState
import wallapp.pixel.selection.SelectionViewState
import wallapp.pixel.separator.Separator
import wallapp.pixel.separator.SeparatorViewState
import wallapp.pixel.spacer.Spacer
import wallapp.pixel.spacer.SpacerViewState

@Composable
fun View(
    render: Render,
    view: View,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
) {
    val viewRenderer = render.viewRenderer.viewRendererCompose
    if (viewRenderer.render(render = render, view = view, modifier = modifier, alignment = alignment)) {
        return
    }

    when (val viewState = view.viewState) {
        is SpacerViewState -> {
            Spacer(viewState, modifier)
        }

        is ImageViewState -> {
            Image(render, viewState, modifier)
        }

        is FeedViewState -> {
            FeedGrid(render, viewState, modifier)
        }

        is SeparatorViewState -> {
            Separator(viewState, modifier)
        }

        is MenuItemViewState -> {
            MenuItem(render, viewState, modifier)
        }

        is SelectionGroupViewState -> {
            SelectionGroup(render, viewState, modifier)
        }

        is SelectionViewState -> {
            Selection(render, viewState, modifier)
        }

        else -> {
            UnhandledView(viewState, modifier)
        }
    }

}

@Composable
private fun UnhandledView(
    viewState: ViewState,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Text("Unhandled ViewState: ${viewState::class.simpleName}")
    }
}