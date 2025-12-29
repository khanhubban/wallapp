package wallapp.image

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.loadImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import wallapp.image.hash.ImageHashDecoder
import wallapp.image.host.ImageHostDisplayTarget
import wallapp.image.host.ImageHostManager
import wallapp.image.host.ImageHostOptionsDefault
import wallapp.image.host.ImageHostUrlMapper
import wallapp.image.scaler.ImageScaler
import wallapp.pixel.compose.pxToDp
import java.io.IOException
import java.net.URL

@Composable
fun ImageUrl(
    imageUrl: String,
    contentDescription: String?,
    loadingImage: Image?,
    imageSize: ImageSize?,
    imageHostManager: ImageHostManager,
    imageHashDecoder: ImageHashDecoder,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    alignment: Alignment = Alignment.Center,
    colorFilter: ColorFilter? = null,
) {
    ImageUrlSeikoImageLoader(
        imageUrl = imageUrl,
        contentDescription = contentDescription,
        loadingImage = loadingImage,
        imageSize = imageSize,
        imageHashDecoder = imageHashDecoder,
        contentScale = contentScale,
        modifier = modifier,
        alignment = alignment,
        colorFilter = colorFilter,
        onImageAction = null,
    )
}


@Composable
fun <T> ImageUrl(
    load: suspend () -> T,
    painterFor: @Composable (T) -> Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
) {
    val image: T? by produceState<T?>(null) {
        value = withContext(Dispatchers.IO) {
            try {
                load()
            } catch (e: IOException) {
                println("Error loading image: ${e.localizedMessage}")
                e.printStackTrace()
                null
            }
        }
    }

    if (image != null) {
        Image(
            painter = painterFor(image!!),
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier
        )
    }
}

fun loadImageBitmap(url: String?): ImageBitmap =
    URL(url).openStream().buffered().use(::loadImageBitmap)

@Composable
private fun ImageUrlDynamic(
    modifier: Modifier,
    imageUrl: String,
    contentDescription: String?,
    loadingImage: Image?,
    imageHostManager: ImageHostManager,
    imageHostUrlMapper: ImageHostUrlMapper,
    imageScaler: ImageScaler,
    imageHashDecoder: ImageHashDecoder,
    contentScale: ContentScale = ContentScale.Fit,
    alignment: Alignment = Alignment.Center,
    colorFilter: ColorFilter? = null,
) {
    var contentHeight by remember { mutableIntStateOf(0) }
    var contentWidth by remember { mutableIntStateOf(0) }

    // Always render the box to ensure the size is always calculated (#151). Note: this will
    // likely be undesired in the event an image is rendered in a view that's size animates.
    Box(
        modifier = modifier
            .onGloballyPositioned {
                contentWidth = it.size.width
                contentHeight = it.size.height
            }
    )

    if (contentWidth > 0 && contentHeight > 0) {
        val platformWidth = contentWidth.pxToDp().value
        val platformHeight = contentHeight.pxToDp().value

        val imageHostDisplayTarget = ImageHostDisplayTarget.InAppCompose
        val imageHostFormat = imageHostManager.getImageHostFormat(imageHostDisplayTarget)
        val dynamicUrlResult = imageHostUrlMapper.mapDynamicImageUrl(
            imageUrl = imageUrl,
            options = ImageHostOptionsDefault(imageHostFormat) {
                imageScaler.apply(this, platformWidth, platformHeight)
                crop = true
            },
            alignment = alignment,
        )
        val exactSize = ImageSize(contentWidth, contentHeight)
//        Log.v("ImageUrlExactImageSize: exactImageUrl: $exactImageUrl, exactSize: $exactSize")

        val onImageAction = imageHostManager.createOnImageAction(
            imageHostDisplayTarget = imageHostDisplayTarget,
            dynamicUrlResult = dynamicUrlResult,
        )

        ImageUrlSeikoImageLoader(
            imageUrl = dynamicUrlResult.url,
            contentDescription = contentDescription,
            loadingImage = loadingImage,
            imageSize = exactSize,
            imageHashDecoder = imageHashDecoder,
            contentScale = contentScale,
            modifier = modifier,
            alignment = alignment,
            colorFilter = colorFilter,
            onImageAction = onImageAction,
        )
    }
}