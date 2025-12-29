package wallapp.ui.content.error

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import wallapp.content.state.error.ErrorViewSpec
import wallapp.content.state.error.ErrorViewState
import wallapp.image.Image
import wallapp.pixel.compose.BackHandler
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.render.Render
import wallapp.pixel.text.Text

@Composable
fun ErrorScreen(
    render: Render,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.weight(.5f))
        Text("(error)")
        Spacer(modifier = Modifier.weight(.5f))
    }
}

@Composable
fun ErrorScreen(
    render: Render,
    viewState: ErrorViewState,
    modifier: Modifier = Modifier,
) {
    when (viewState) {
        ErrorViewState.Loading -> { }

        is ErrorViewState.Data -> {
            ErrorScreen(
                render = render,
                viewState = viewState,
                modifier = modifier,
            )
        }

        is ErrorViewState.Placeholder -> {
            ErrorScreen(
                render = render,
                viewState = viewState,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun ErrorScreen(
    render: Render,
    viewState: ErrorViewState.Data,
    modifier: Modifier = Modifier,
) {
    val paddingSmall = render.defaultViewSpec.paddingSmall
    val paddingDefault = render.defaultViewSpec.paddingDefault
    val paddingLarge = render.defaultViewSpec.paddingLarge
    val statusBarHeight = render.windowFrame.statusBarHeight

    val viewSpec = viewState.viewSpec

    val title = viewState.title
    val image = viewState.image
    val message1 = viewState.message1
    val message2 = viewState.message2
    val errorMessage = viewState.errorMessage
    val actionButton = viewState.actionButton
    val actionButton2 = viewState.actionButton2
    val closeButton = viewState.closeButton

    BackHandler(enabled = !viewState.allowNavigateBack) {
        // Do nothing
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        if (closeButton != null) {
            MenuItem(
                render = render,
                menuItem = closeButton,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = statusBarHeight + paddingSmall, start = paddingSmall),
            )
        }

        Column(
            modifier = Modifier
                .padding(horizontal = paddingLarge)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.weight(.5f))

            ErrorContent(render, viewSpec, title, image, message1, message2)

            Spacer(modifier = Modifier.weight(.5f))

            if (actionButton != null) {
                ErrorFooter(
                    render,
                    actionButton,
                    actionButton2,
                    errorMessage,
                )
            }
        }

    }

}

@Composable
private fun ColumnScope.ErrorContent(
    render: Render,
    viewSpec: ErrorViewSpec,
    title: MenuItem,
    image: MenuItem?,
    message1: MenuItem?,
    message2: MenuItem?,
) {
    val imageSize = viewSpec.imageSize
    val paddingDefault = render.defaultViewSpec.paddingDefault
    val paddingLarge = render.defaultViewSpec.paddingLarge

    MenuItem(
        render = render,
        menuItem = title,
    )

    Spacer(modifier = Modifier.height(paddingLarge))

    if (image != null) {
        MenuItem(
            render = render,
            menuItem = image,
            modifier = Modifier
                .size(imageSize),
        )
    }

    Spacer(modifier = Modifier.height(paddingLarge))

    if (message1 != null) {
        MenuItem(
            render = render,
            message1,
            modifier = Modifier
                .fillMaxWidth(),
        )
    }
    if (message2 != null) {
        Spacer(modifier = Modifier.height(paddingDefault))
        MenuItem(
            render = render,
            message2,
            modifier = Modifier
                .fillMaxWidth(),
        )
    }
}

@Composable
private fun ColumnScope.ErrorFooter(
    render: Render,
    actionButton: MenuItem,
    actionButton2: MenuItem?,
    errorMessage: Text?,
    modifier: Modifier = Modifier,
) {
    val paddingDefault = render.defaultViewSpec.paddingDefault
    val navigationBarHeight = render.windowFrame.navigationBarHeight
    val bottomPadding = navigationBarHeight + paddingDefault

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = paddingDefault)
            .padding(bottom = bottomPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (errorMessage != null) {
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                Text(errorMessage)
            }
            Spacer(modifier = Modifier.height(paddingDefault))
        }

        MenuItem(
            render = render,
            menuItem = actionButton,
        )

        if (actionButton2 != null) {
            Spacer(modifier = Modifier.height(paddingDefault))
            MenuItem(
                render = render,
                menuItem = actionButton2,
            )
        }
    }
}


@Composable
private fun ErrorScreen(
    render: Render,
    viewState: ErrorViewState.Placeholder,
    modifier: Modifier,
) {
    val paddingSmall = render.defaultViewSpec.paddingSmall
    val paddingLarge = render.defaultViewSpec.paddingLarge
    val statusBarHeight = render.windowFrame.statusBarHeight

    val image = viewState.image
    val closeButton = viewState.closeButton

    Box(
        modifier = modifier
            .fillMaxSize(),
    ) {
        Image(
            render,
            image = image,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = statusBarHeight + 56.dp, bottom = paddingLarge)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.secondary,
                ),
            contentScale = ContentScale.FillBounds,
        )

        MenuItem(
            render = render,
            menuItem = closeButton,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = statusBarHeight + paddingSmall, start = paddingSmall),
        )
    }
}