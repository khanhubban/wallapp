package wallapp.image

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import wallapp.image.hash.ImageHashDecoder
import wallapp.resource.ImageHash
import wallapp.resource.ImageHash.ImageHashBlur
import androidx.compose.foundation.Image as ImageCompose

val LocalImageHashAnimate = compositionLocalOf<Boolean> {
    error("No LocalImageHashAnimate provided")
}

@Composable
fun ImageHash(
    imageHashDecoder: ImageHashDecoder,
    imageHash: ImageHash,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    when (imageHash) {
        is ImageHashBlur -> {
            ImageHashBlur(
                imageHashDecoder = imageHashDecoder,
                imageHashBlur = imageHash,
                contentDescription = contentDescription,
                modifier = modifier,
            )
        }
    }
}

@Composable
fun ImageHashBlur(
    imageHashDecoder: ImageHashDecoder,
    imageHashBlur: ImageHashBlur,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.FillBounds,
    /**
     * By definition, the image hash is a small image with blurry appearance. As such, ensure it
     * only takes up a small amount of memory.
     */
    bitmapScale: Float = .2f,
) {
    val animate = LocalImageHashAnimate.current

    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var contentSize by remember { mutableStateOf(IntSize.Zero) }

    LaunchedEffect(imageHashBlur, contentSize) {
        if (contentSize == IntSize.Zero) {
            return@LaunchedEffect
        }
        val width = (contentSize.width * bitmapScale).toInt()
        val height = (contentSize.height * bitmapScale).toInt()
        // This will only occur if there is a data error elsewhere, but it's better to handle it
        if (width <= 0 || height <= 0) {
            return@LaunchedEffect
        }
        imageBitmap = withContext(Dispatchers.IO) {
            imageHashDecoder.decode(imageHashBlur, width, height).let {
                when (it) {
                    is ImageHashDecoder.Result.SuccessCompose -> it.imageBitmap
                    is ImageHashDecoder.Result.Failure, is ImageHashDecoder.Result.SuccessData -> null
                }
            }
        }
    }

    val bitmap = imageBitmap
    if (bitmap != null) {
        val animatedAlpha = if (animate) {
            AnimatedPulsatingAlpha()
        } else {
            1f
        }

        ImageCompose(
            bitmap = bitmap,
            contentDescription = contentDescription,
            modifier = modifier
                .size(width = contentSize.width.dp, height = contentSize.height.dp)
                .alpha(animatedAlpha),
            contentScale = contentScale,
        )
    } else {
        Box(
            modifier = modifier
                .onGloballyPositioned { contentSize = it.size },
        ) {
        }
    }
}

@Composable
private fun AnimatedPulsatingAlpha(
    pulseAnimDuration: Int = 666,
    minTargetAlpha: Float = .875f,
    maxTargetAlpha: Float = 1f,
): Float {
    var targetAlpha by remember { mutableStateOf(minTargetAlpha) }

    val animationSpec = infiniteRepeatable(
        animation = tween<Float>(durationMillis = pulseAnimDuration),
        repeatMode = RepeatMode.Reverse
    )
    val animatedAlpha by animateFloatAsState(targetValue = targetAlpha, animationSpec)

    if (animatedAlpha == targetAlpha) {
        targetAlpha = if (targetAlpha == minTargetAlpha) {
            maxTargetAlpha
        } else {
            minTargetAlpha
        }
    }

    return animatedAlpha
}
