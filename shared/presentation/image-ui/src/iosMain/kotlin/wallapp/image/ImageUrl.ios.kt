package wallapp.image

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import wallapp.image.hash.ImageHashDecoder
import wallapp.image.host.ImageHostManager

@Composable
fun ImageUrl(
    imageUrl: String,
    contentDescription: String?,
    loadingImage: Image?,
    imageSize: ImageSize?,
    imageHostManager: ImageHostManager,
    modifier: Modifier = Modifier,
    imageHashDecoder: ImageHashDecoder,
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

//@Composable
//private fun ImageUrlDynamic(
//    modifier: Modifier,
//    imageUrl: String,
//    contentDescription: String?,
//    loadingImage: Image?,
//    imageHostManager: ImageHostManager,
//    imageHostUrlMapper: ImageHostUrlMapper,
//    imageScaler: ImageScaler,
//    imageHashDecoder: ImageHashDecoder,
//    contentScale: ContentScale = ContentScale.Fit,
//    alignment: Alignment = Alignment.Center,
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
//        val exactSize = ImageSize(contentWidth, contentHeight)
////        Log.v("ImageUrlExactImageSize: exactImageUrl: $exactImageUrl, exactSize: $exactSize")
//
//        val onImageAction = imageHostManager.createOnImageAction(
//            imageHostDisplayTarget = imageHostDisplayTarget,
//            dynamicUrlResult = dynamicUrlResult,
//        )
//
//        ImageUrl(
//            modifier,
//            imageUrl = dynamicUrlResult.url,
//            contentDescription,
//            loadingImage = loadingImage,
//            imageSize = exactSize,
//            imageHashDecoder,
//            contentScale,
//            alignment,
//            colorFilter,
//            onImageAction = onImageAction,
//        )
//    }
//}
