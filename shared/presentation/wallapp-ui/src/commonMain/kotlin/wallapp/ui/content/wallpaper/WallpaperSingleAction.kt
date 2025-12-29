package wallapp.ui.content.wallpaper

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import wallapp.content.state.wallpaper.WallpaperSingleActionViewState
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.render.Render
import wallapp.pixel.toolbar.Toolbar
import wallapp.ui.content.loading.Loading

@Composable
fun WallpaperSingleAction(
    render: Render,
    viewState: WallpaperSingleActionViewState,
    modifier: Modifier = Modifier,
) {
    when (viewState) {
        is WallpaperSingleActionViewState.Loading -> {
            Loading(render = render, modifier = modifier.fillMaxWidth())
        }

        is WallpaperSingleActionViewState.Success -> {
            WallpaperSingleAction(render = render, viewState = viewState, modifier = modifier)
        }
    }
}

@Composable
fun WallpaperSingleAction(
    render: Render,
    viewState: WallpaperSingleActionViewState.Success,
    modifier: Modifier = Modifier,
) {
    val viewSpec = viewState.viewSpec
    val horizontalPadding = viewSpec.horizontalPadding
    val paddingDefault = render.defaultViewSpec.paddingDefault
    val paddingSmall = render.defaultViewSpec.paddingSmall
    val height = viewSpec.height

    val toolbar = viewState.toolbarViewState
    val actionButton1 = viewState.actionButton1
    val actionButton2 = viewState.actionButton2
    val actionButton3 = viewState.actionButton3

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(MaterialTheme.colorScheme.surface)
            .padding(
                start = horizontalPadding,
                end = horizontalPadding,
                bottom = paddingDefault,
            ),
    ) {
        Spacer(modifier = Modifier.height(paddingSmall))
        Toolbar(render, toolbar)
        Spacer(modifier = Modifier.height(paddingSmall))

        MenuItem(
            render,
            actionButton1,
            Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(paddingDefault))

        MenuItem(
            render,
            actionButton2,
            Modifier.fillMaxWidth()
        )

        actionButton3?.let {
            Spacer(modifier = Modifier.height(paddingDefault))
            MenuItem(
                render,
                it,
                Modifier.fillMaxWidth()
            )
        }
    }
}