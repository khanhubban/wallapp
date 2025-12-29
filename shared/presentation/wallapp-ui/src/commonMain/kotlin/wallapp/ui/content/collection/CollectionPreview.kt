package wallapp.ui.content.collection

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import wallapp.content.state.collection.CollectionPreviewFooterViewState
import wallapp.content.state.collection.CollectionPreviewViewState
import wallapp.image.Image
import wallapp.pixel.compose.clickableIfNotNull
import wallapp.pixel.render.Render
import wallapp.pixel.shape.clip
import wallapp.pixel.text.Text
import wallapp.pixel.view.onClick
import wallapp.ui.content.upgrade.plus.indicator.PlusIndicator

@Composable
fun CollectionPreview(
    render: Render,
    collectionPreview: CollectionPreviewViewState,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
) {
    CollectionPreviewStack(
        render = render,
        collectionPreview = collectionPreview,
        modifier = modifier,
        alignment = alignment,
    )
}

@Composable
fun CollectionPreviewStack(
    render: Render,
    collectionPreview: CollectionPreviewViewState,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
) {
    val viewSpec = collectionPreview.viewSpec
    val layers = collectionPreview.layers.mapToStackItems(collectionPreview)
    val width = viewSpec.width//?.dp
    val height = viewSpec.height

    Box(
        modifier = modifier
            .width(width)
            .height(height),
    ) {
        if (layers != null) {
            CollectionPreviewStack(render, layers, alignment = alignment, title = collectionPreview.title)
//    ImageStack(
//        render = render,
//        items = heroImages,
//        modifier = modifier
//            .fillMaxSize(),
//    )
        }
    }
}

@Composable
fun CollectionPreviewStack(
    render: Render,
    layers: List<CollectionStackLayer>,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    title: String,
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
    ) {
        layers.forEach { layer ->
            CollectionPreviewItem(
                render = render,
                layer = layer,
                alignment = alignment,
                title = title,
            )
        }
    }
}

@Composable
private fun CollectionPreviewFooter(
    render: Render,
    viewState: CollectionPreviewFooterViewState,
    contentColor: Color,
    modifier: Modifier = Modifier,
) {
    val paddingDefault = render.defaultViewSpec.paddingDefault
    val shapeSpec = viewState.shapeSpec

    val title = viewState.title
    val price = viewState.price
    val onClick = viewState.onClick?.onClick
    val scrimImage = viewState.scrimImage

    Box(
        modifier
            .clip(shapeSpec, render.shapeClipper)
            .clickableIfNotNull(render, onClick),
    ) {
        Image(
            render,
            image = scrimImage,
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.FillBounds,
        )

        Text(
            title,
            modifier = Modifier
                .padding(horizontal = paddingDefault)
                .align(Alignment.CenterStart),
            colorOverride = contentColor,
        )

        if (price != null) {
            Text(
                price,
                modifier = Modifier
                    .padding(horizontal = paddingDefault)
                    .align(Alignment.CenterEnd),
                colorOverride = contentColor,
            )
        }
    }
}

@Composable
private fun ColumnScope.CollectionPreviewItem(
    render: Render,
    layer: CollectionStackLayer,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentColor: Color = Color.White,
    title: String,
) {
    val paddingSmall = render.defaultViewSpec.paddingSmall
    val shadowImage = layer.shadowImage
    val plusIndicator = layer.plusIndicator
    val shapeClipper = render.shapeClipper
    val footer = layer.footer
    val footerHeight = layer.footerHeight?.dp
    val width = layer.imageViewState.viewSpec.width
    val height = layer.imageViewState.viewSpec.height

    val plusIndicatorYOffset = if (footer != null) {
        footerHeight ?: 0.dp
    } else {
        0.dp
    }

    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .align(Alignment.CenterHorizontally),
    ) {
        Image(
            render,
            viewState = layer.imageViewState,
            modifier = Modifier
                .fillMaxSize(),
            alignment = alignment,
            onClick = { layer.eventHandler.invoke() },
        )

        if (footer != null) {
            CollectionPreviewFooter(
                render,
                viewState = footer,
                contentColor = contentColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .height(footerHeight!!)
                    .clip(footer.shapeSpec, shapeClipper),
            )
        }

        if (plusIndicator != null) {
            PlusIndicator(
                render,
                viewState = plusIndicator,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = paddingSmall, bottom = paddingSmall + plusIndicatorYOffset)
                    .semantics(mergeDescendants = true) {
                        contentDescription = title
                    },
            )
        }

        if (shadowImage != null) {
            Image(
                render,
                image = shadowImage,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .align(Alignment.TopCenter),
                contentScale = ContentScale.FillBounds,
            )
        }
    }
}
