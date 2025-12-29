package wallapp.image

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import wallapp.image.Image.ImageResource
import wallapp.image.cache.ImageCacheKeyManager
import wallapp.image.hash.ImageHashDecoder
import wallapp.image.host.ImageHostManager
import wallapp.image.host.ImageHostManagerNoOp
import wallapp.image.loader.ImageLoader
import wallapp.pixel.clickable.clickable
import wallapp.pixel.compose.FullWidthDp
import wallapp.pixel.compose.conditional
import wallapp.pixel.compose.conditionalComposable
import wallapp.pixel.image.ImageViewState
import wallapp.pixel.render.Render
import wallapp.pixel.shape.clip
import wallapp.pixel.view.UIKitFactory
import wallapp.pixel.view.ViewContentScaleMapperCompose


@Composable
fun ImageInternal(
    image: Image,
    imageSize: ImageSize?,
    imageVideoState: ImageVideoState?,
    imageHashDecoder: ImageHashDecoder,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    colorFilter: ColorFilter? = null,
    alignment: Alignment = Alignment.Center,
) {
    val (success: ImageResource, loading: Image?)  = when (image) {
        is ImageResource -> {
            image to null
        }

        is Image.ImageStates -> {
            image.success as ImageResource to image.loading
        }
    }

    ImageResource(
        image = success,
        imageVideoState = imageVideoState,
        loadingImage = loading,
        imageSize = imageSize,
        imageLoader = null,
        imageMemoryCacheKeyManager = null,
        imageHostManager = ImageHostManagerNoOp,
        imageHashDecoder = imageHashDecoder,
        modifier = modifier,
        contentScale = contentScale,
        colorFilter = colorFilter,
        alignment = alignment,
    )
}

@Composable
fun Image(
    image: Image,
    imageSize: ImageSize?,
    imageVideoState: ImageVideoState?,
    imageLoader: ImageLoader,
    imageMemoryCacheKeyManager: ImageCacheKeyManager,
    imageHostManager: ImageHostManager,
    imageHashDecoder: ImageHashDecoder,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    colorFilter: ColorFilter? = null,
    alignment: Alignment = Alignment.Center,
    uiKitFactory: UIKitFactory? = null,
) {
    val (success: ImageResource, loading: Image?)  = when (image) {
        is ImageResource -> {
            image to null
        }

        is Image.ImageStates -> {
            image.success as ImageResource to image.loading
        }
    }

    ImageResource(
        image = success,
        imageSize = imageSize,
        imageVideoState = imageVideoState,
        loadingImage = loading,
        imageLoader,
        imageMemoryCacheKeyManager,
        imageHostManager,
        imageHashDecoder,
        modifier,
        contentScale,
        colorFilter,
        alignment,
        uiKitFactory,
    )
}

@Composable
fun Image(
    render: Render,
    image: Image,
    modifier: Modifier = Modifier,
    imageVideoState: ImageVideoState? = null,
    contentScale: ContentScale = ContentScale.Fit,
    colorFilter: ColorFilter? = null,
    alignment: Alignment = Alignment.Center,
    animateLoading: Boolean = false,
    imageSize: ImageSize? = null,
) {
    CompositionLocalProvider(LocalImageHashAnimate provides animateLoading) {
        Image(
            image = image,
            imageSize = imageSize,
            imageVideoState = imageVideoState,
            imageLoader = render.imageLoader,
            imageMemoryCacheKeyManager = render.imageMemoryCacheKeyManager,
            imageHostManager = render.imageHostManager,
            imageHashDecoder = render.imageHashDecoder,
            modifier,
            contentScale,
            colorFilter,
            alignment,
            uiKitFactory = render.uiKitFactory,
        )
    }
}

@Composable
fun Image(
    render: Render,
    viewState: ImageViewState,
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
    alignment: Alignment = Alignment.Center,
    animateLoading: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val viewSpec = viewState.viewSpec
    val imageVideoSpec = viewState.videoState
    val width = viewSpec.width
    val height = viewSpec.height
    val shapeSpec = viewSpec.shapeSpec
    val contentScale = ViewContentScaleMapperCompose.map(viewSpec.contentScale)

    Image(
        render = render,
        image = viewState.image,
        imageSize = viewState.imageSize,
        imageVideoState = imageVideoSpec,
        modifier = modifier
            .conditional(width != FullWidthDp) { width(width) }
            .height(height)
            .conditionalComposable(shapeSpec != null) { clip(shapeSpec!!, render.shapeClipper) }
            .conditional(onClick != null) { clickable(render, onClick!!) },
        contentScale = contentScale,
        colorFilter = colorFilter,
        alignment = alignment,
        animateLoading = animateLoading,
    )
}