package wallapp.ui.content.folder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import wallapp.content.state.folder.FolderToolbarViewSpec
import wallapp.content.state.folder.FolderToolbarViewState
import wallapp.pixel.compose.statusBarsPadding
import wallapp.pixel.render.Render
import wallapp.pixel.text.Text
import wallapp.pixel.text.TextCentered
import wallapp.pixel.theme.AppTheme
import wallapp.pixel.theme.dynamicColorScheme
import wallapp.pixel.toolbar.Toolbar
import wallapp.pixel.toolbar.ToolbarViewState


@Composable
fun FolderToolbar(
    render: Render,
    viewState: FolderToolbarViewState,
    toolbarViewSpec: FolderToolbarViewSpec,
    modifier: Modifier = Modifier,
) {

    val colorScheme = dynamicColorScheme(themeColors = viewState.theme.themeColors)

    val name = viewState.name
    val toolbar = viewState.toolbarViewState

    AppTheme(
        render = render,
        colorScheme = colorScheme,
    ) {
        FolderToolbar(render, name, toolbar, toolbarViewSpec, modifier)
    }
}

@Composable
fun FolderToolbar(
    render: Render,
    name: Text,
    toolbar: ToolbarViewState,
    toolbarViewSpec: FolderToolbarViewSpec,
    modifier: Modifier = Modifier,
) {

    val minToolbarHeight = toolbarViewSpec.minToolbarHeight
    val maxToolbarHeight = toolbarViewSpec.maxToolbarHeight

    val background = MaterialTheme.colorScheme.background
    val onBackground = MaterialTheme.colorScheme.onBackground

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(background)
            .statusBarsPadding(render.windowFrame)
            .heightIn(minToolbarHeight)
            .height(maxToolbarHeight)
    ) {
        Toolbar(
            render,
            toolbar,
            modifier = Modifier
                .fillMaxWidth()
                .height(minToolbarHeight),
        )
        TextCentered(
            text = name,
            height = toolbarViewSpec.titleHeight,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = toolbarViewSpec.titleTop),
            colorOverride = onBackground,
        )
    }

}