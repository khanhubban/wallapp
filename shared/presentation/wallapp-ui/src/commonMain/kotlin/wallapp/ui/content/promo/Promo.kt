package wallapp.ui.content.promo

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import wallapp.content.state.promo.PromoViewState
import wallapp.image.Image
import wallapp.pixel.clickable.clickable
import wallapp.pixel.render.Render

@Composable
fun Promo(
    render: Render,
    viewState: PromoViewState,
    modifier: Modifier = Modifier,
) {
    Image(
        render = render,
        image = viewState.image,
        modifier = modifier
            .fillMaxWidth()
            .clickable(render) { viewState.viewEventHandler() },
    )
}