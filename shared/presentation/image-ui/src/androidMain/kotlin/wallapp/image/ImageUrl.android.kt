@file:Suppress("UNUSED_PARAMETER")

package wallapp.image

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import wallapp.image.cache.ImageCacheKeyManager
import wallapp.image.hash.ImageHashDecoder
import wallapp.image.host.ImageHostManager
import wallapp.image.loader.ImageLoader
import wallapp.image.loader.ImageLoaderCoil
import wallapp.pixel.compose.clipIfNotNull

@Composable
fun ImageUrl(
    imageUrl: String,
    contentDescription: String?,
    loadingImage: Image?,
    shape: Shape?,
    imageSize: ImageSize?,
    imageLoader: ImageLoader,
    imageMemoryCacheKeyManager: ImageCacheKeyManager,
    imageHostManager: ImageHostManager,
    imageHashDecoder: ImageHashDecoder,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    colorFilter: ColorFilter? = null,
) {
    ImageUrl(
        modifier
            .clipIfNotNull(shape)
            .padding(0.dp),
        imageUrl,
        contentDescription,
        loadingImage = loadingImage,
        imageSize = imageSize,
        contentScale,
        alignment,
        imageLoader,
        imageMemoryCacheKeyManager,
        imageHashDecoder,
        colorFilter = colorFilter,
        onImageAction = null,
    )
}

//
@Composable
private fun ImageUrl(
    modifier: Modifier,
    imageUrl: String,
    contentDescription: String?,
    loadingImage: Image?,
    imageSize: ImageSize?,
    contentScale: ContentScale = ContentScale.Fit,
    alignment: Alignment = Alignment.Center,
    imageLoader: ImageLoader,
    imageMemoryCacheKeyManager: ImageCacheKeyManager,
    imageHashDecoder: ImageHashDecoder,
    colorFilter: ColorFilter? = null,
    onImageAction: OnImageAction?,
) {
    val useCoil = imageLoader is ImageLoaderCoil

    if (useCoil) {
        ImageUrlCoil(
            modifier,
            imageUrl,
            contentDescription,
            contentScale,
            alignment,
            imageSize,
            imageLoader,
            imageMemoryCacheKeyManager,
            colorFilter,
        )
    } else {
        ImageUrlSeikoImageLoader(
            modifier,
            imageUrl,
            contentDescription,
            loadingImage,
            imageSize,
            imageHashDecoder,
            contentScale,
            alignment,
            colorFilter,
            onImageAction,
        )
    }
}

//@Composable
//private fun ImageUrlDynamic(
//    modifier: Modifier,
//    imageUrl: String,
//    contentDescription: String?,
//    loadingImage: Image?,
//    contentScale: ContentScale = ContentScale.Fit,
//    alignment: Alignment = Alignment.Center,
//    imageLoader: ImageLoader,
//    imageMemoryCacheKeyManager: ImageCacheKeyManager,
//    imageHostManager: ImageHostManager,
//    imageHostUrlMapper: ImageHostUrlMapper,
//    imageScaler: ImageScaler,
//    imageHashDecoder: ImageHashDecoder,
//    colorFilter: ColorFilter? = null,
//) {
//    var contentHeight by remember { mutableIntStateOf(0) }
//    var contentWidth by remember { mutableIntStateOf(0) }
//
//    // Always render the box to ensure the size is always calculated (#151). Note: this will
//    // likely be undesired in the event an image is rendered in a view that's size animates.
//    Box(
//        modifier = modifier
//            .onGloballyPositioned {
//                contentWidth = it.size.width
//                contentHeight = it.size.height
//            }
//    )
//
//    if (contentWidth > 0 && contentHeight > 0) {
//        val platformWidth = contentWidth.pxToDp().value
//        val platformHeight = contentHeight.pxToDp().value
//
//        val imageHostDisplayTarget = ImageHostDisplayTarget.InAppCompose
//        val imageHostFormat = imageHostManager.getImageHostFormat(imageHostDisplayTarget)
//        val dynamicUrlResult = imageHostUrlMapper.mapDynamicImageUrl(
//            imageUrl = imageUrl,
//            options = ImageHostOptionsDefault(imageHostFormat) {
//                imageScaler.apply(this, platformWidth, platformHeight)
//                crop = true
//            },
//            alignment = alignment,
//        )
//
//        val onImageAction = imageHostManager.createOnImageAction(
//            imageHostDisplayTarget = imageHostDisplayTarget,
//            dynamicUrlResult = dynamicUrlResult,
//        )
//
//        val exactSize = ImageSize(contentWidth, contentHeight)
////        Log.v("ImageUrlExactImageSize: exactImageUrl: $exactImageUrl, exactSize: $exactSize")
//        ImageUrl(
//            modifier,
//            imageUrl = dynamicUrlResult.url,
//            contentDescription,
//            loadingImage,
//            imageSize = exactSize,
//            contentScale,
//            alignment = alignment,
//            imageLoader,
//            imageMemoryCacheKeyManager,
//            imageHostUrlMapper,
//            imageScaler,
//            imageHashDecoder,
//            colorFilter,
//            onImageAction = onImageAction,
//        )
//    }
//}
