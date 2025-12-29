package wallapp.ui.content.collection

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import me.onebone.toolbar.CollapsingToolbarScope
import wallapp.pixel.animation.AnimatedViewSpec
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.render.Render
import wallapp.pixel.toolbar.CollapsingToolbarContent

@Composable
fun CollapsingToolbarScope.CollectionLockedButton(
    render: Render,
    progress: Float,
    button: MenuItem,
    animatedViewSpec: AnimatedViewSpec,
    modifier: Modifier = Modifier,
) {
    CollapsingToolbarContent(
        render,
        modifier = modifier,
        animatedViewSpec = animatedViewSpec,
        progress = progress,
    ) { mod, _ ->
        CollectionLockedButton(render, button, mod)
    }
}

@Composable
private fun CollectionLockedButton(
    render: Render,
    button: MenuItem,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
    ) {
        MenuItem(
            render = render,
            menuItem = button,
            modifier = Modifier.align(Alignment.Center).fillMaxWidth(),
        )
    }
}

