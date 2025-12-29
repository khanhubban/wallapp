package wallapp.image

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import com.seiko.imageloader.rememberImageSuccessPainter
import com.seiko.imageloader.ui.AutoSizeBox
import wallapp.image.hash.ImageHashDecoder
import wallapp.image.host.ImageHostUrlUnmapper.extractExpiresEpochFromUrl
import wallapp.image.seiko.seikoImageLoaderSizeResolverWorkaround
import wallapp.log.Log
import wallapp.time.formatEpoch
import androidx.compose.foundation.Image as ComposeImage
import com.seiko.imageloader.model.ImageAction as SeikoImageAction
import com.seiko.imageloader.model.ImageRequest as SeikoImageRequest


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ImageUrlSeikoImageLoader(
    modifier: Modifier,
    imageUrl: String,
    contentDescription: String?,
    loadingImage: Image?,
    imageSize: ImageSize?,
    imageHashDecoder: ImageHashDecoder,
    contentScale: ContentScale = ContentScale.Fit,
    alignment: Alignment = Alignment.Center,
    colorFilter: ColorFilter? = null,
    onImageAction: OnImageAction?,
) {
    Log.v("ImageUrl [imageloader]: ${extractExpiresEpochFromUrl(imageUrl)?.let { "(Expiration: ${formatEpoch(it)}}" } ?:"" } $imageUrl")

    val actionModifier = Modifier.fillMaxSize()
    val size = imageSize?.size
    val imageRequest = remember(size, imageUrl) {
        SeikoImageRequest {
            data(imageUrl)

            size?.seikoImageLoaderSizeResolverWorkaround?.also {
                // Override the default behaviour for AutoSizeBox. It uses the view's smaller of
                // the two dimensions to calculate the maxSize of the decoded image. This is not
                // ideal for images that are not close to square. (#156)
                size(it)
            }
        }
    }
    AutoSizeBox(
        request = imageRequest,
        modifier = modifier,
    ) { action ->
        if (action is SeikoImageAction.Failure) {
            Log.e("Failed to load image: $imageUrl, error: ${action.error}\n${action.error.stackTraceToString()}")
        }

        var imageLoadState by remember { mutableStateOf(action) }
        var showLoadingImage by remember { mutableStateOf(loadingImage != null) }

        imageLoadState = action
        onImageAction?.invoke(mapSeikoImageAction(imageUrl, action))

        // TODO: Handle ImageAction.Failure

        if (loadingImage != null) {
            AnimatedVisibility(
                visible = showLoadingImage,
                exit = fadeOut(animationSpec = tween(durationMillis = 300))
            ) {
                ImageInternal(
                    image = loadingImage,
                    imageSize = imageSize,
                    imageVideoState = null,
                    imageHashDecoder = imageHashDecoder,
                    modifier = actionModifier,
                    contentScale = contentScale,
                    alignment = alignment,
                    colorFilter = colorFilter,
                )
//                Box(modifier = actionModifier.background(loadingColor))
            }
        }

        AnimatedVisibility(
            visible = action is SeikoImageAction.Success,
            enter = fadeIn(animationSpec = tween(durationMillis = 300)),
        ) {
            if (action is SeikoImageAction.Success) {
                ComposeImage(
                    rememberImageSuccessPainter(action),
                    contentDescription = contentDescription,
                    modifier = actionModifier,
                    contentScale = contentScale,
                    alignment = alignment,
                    colorFilter = colorFilter,
                )

                if (transition.currentState == transition.targetState){
                    showLoadingImage = false
                }
            }
        }
    }
}
