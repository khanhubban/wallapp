package wallapp.ui.content.upgrade.plus.indicator

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import wallapp.content.state.upgrade.plus.indicator.PlusIndicatorViewState
import wallapp.image.Image
import wallapp.pixel.clickable.clickable
import wallapp.pixel.compose.ifNonNull
import wallapp.pixel.render.Render
import wallapp.pixel.view.onClick

@Composable
fun PlusIndicator(
    render: Render,
    viewState: PlusIndicatorViewState,
    modifier: Modifier = Modifier,
) {
    val image = viewState.image
    val height = viewState.height
    val width = viewState.width
    val onClick: (() -> Unit)? = viewState.viewEventHandler?.onClick

    Image(
        render = render,
        image = image,
        modifier = modifier
            .width(width)
            .height(height)
            .ifNonNull(onClick) { clickable(render) { it() } }
    )
}