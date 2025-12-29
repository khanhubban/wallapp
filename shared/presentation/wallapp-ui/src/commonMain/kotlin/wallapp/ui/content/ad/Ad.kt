package wallapp.ui.content.ad

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import wallapp.content.state.ad.AdViewSpec
import wallapp.content.state.ad.AdViewState
import wallapp.image.Image
import wallapp.pixel.clickable.clickable
import wallapp.pixel.compose.paddingAx
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.render.Render
import wallapp.pixel.shape.ShapeSpec
import wallapp.pixel.shape.clip
import wallapp.pixel.text.Text
import wallapp.pixel.view.onClick


@Composable
fun Ad(
    render: Render,
    viewState: AdViewState,
    modifier: Modifier = Modifier,
) {
    Ad(
        modifier,
        render,
        viewSpec = viewState.viewSpec,
        shapeSpec = viewState.shapeSpec,
        image = viewState.image,
        heading = viewState.title,
        summary = viewState.summary,
        heroButton = viewState.heroButton,
        closeButton = viewState.closeButton,
        onClick = viewState.viewEventHandler.onClick,
    )
}

@Composable
fun Ad(
    modifier: Modifier = Modifier,
    render: Render,
    viewSpec: AdViewSpec,
    shapeSpec: ShapeSpec?,
    image: Image,
    heading: Text,
    summary: Text,
    heroButton: MenuItem,
    closeButton: MenuItem?,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
//    textColor: Color = contentColorFor(backgroundColor),
    onClick: () -> Unit = {},
) {
    val height = viewSpec.height
    val imageHeight = viewSpec.imageHeight
    val paddingDefault = render.defaultViewSpec.paddingDefault
    val closeButtonPadding = viewSpec.closeButtonPadding

    Box(
        modifier = modifier
            .height(height)
            .clip(shapeSpec, render.shapeClipper)
            .background(backgroundColor)
            .clickable(render) { onClick.invoke() }
    ) {
        if (closeButton != null) {
            MenuItem(
                render,
                closeButton,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .paddingAx(closeButtonPadding),
            )
        }

        Column(
            modifier = Modifier
                .padding(horizontal = paddingDefault),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = heading,
                modifier = Modifier.padding(vertical = 16.dp),
            )
            Image(
                image,
                imageSize = null,
                imageVideoState = null,
                render.imageLoader,
                render.imageMemoryCacheKeyManager,
                render.imageHostManager,
                render.imageHashDecoder,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .height(imageHeight)
                    .clip(MaterialTheme.shapes.medium),
            )
            summary?.also {
                Text(
                    text = summary,
                    modifier = Modifier.padding(vertical = 16.dp),
                )
            }
            heroButton?.also {
                MenuItem(
                    render,
                    it,
                )
            }
        }
    }

}