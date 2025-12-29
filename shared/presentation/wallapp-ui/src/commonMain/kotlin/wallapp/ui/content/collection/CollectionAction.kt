package wallapp.ui.content.collection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import wallapp.content.state.collection.CollectionActionViewState
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.render.Render
import wallapp.pixel.toolbar.Toolbar
import wallapp.ui.content.loading.Loading

@Composable
fun CollectionAction(
    render: Render,
    viewState: CollectionActionViewState,
    modifier: Modifier = Modifier,
) {
    when (viewState) {
        is CollectionActionViewState.Loading -> {
            Loading(render = render, modifier = modifier.fillMaxWidth())
        }

        is CollectionActionViewState.Success -> {
            CollectionAction(render = render, viewState = viewState, modifier = modifier)
        }
    }
}

@Composable
fun CollectionAction(
    render: Render,
    viewState: CollectionActionViewState.Success,
    modifier: Modifier = Modifier,
) {
    val viewSpec = viewState.viewSpec
    val paddingSmall = render.defaultViewSpec.paddingSmall
    val paddingDefault = render.defaultViewSpec.paddingDefault
    val horizontalPadding = viewSpec.horizontalPadding
    val height = viewSpec.height

    val toolbar = viewState.toolbarViewState
    val actionButton1 = viewState.actionButton1
    val actionButton2 = viewState.actionButton2
    val infoMessage = viewState.infoMessage

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
            Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(paddingDefault))

        actionButton2?.also {
            MenuItem(
                render,
                it,
                Modifier.fillMaxWidth(),
            )
        }

        infoMessage?.also {
            Spacer(modifier = Modifier.height(paddingDefault))
            MenuItem(
                render,
                it,
                Modifier.fillMaxWidth(),
            )
        }
    }
}