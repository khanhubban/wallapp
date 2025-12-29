package wallapp.image

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition
import wallapp.resource.AnimatedImageClipSpec
import wallapp.resource.AnimatedImageSpec
import wallapp.resource.rawRes

val AnimatedImageClipSpec.lottieClipSpec: LottieClipSpec
    get() = when (this) {
        is AnimatedImageClipSpec.Frame -> {
            LottieClipSpec.Frame(
                min = min,
                max = max,
                maxInclusive = maxInclusive,
            )
        }

        is AnimatedImageClipSpec.Progress -> {
            LottieClipSpec.Progress(
                min = min,
                max = max,
            )
        }
    }

@Composable
fun ImageLottie(
    animatedImageSpec: AnimatedImageSpec,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
) {
    val speed = animatedImageSpec.speed
    val iterations = if (animatedImageSpec.infiniteRepeat) {
        LottieConstants.IterateForever
    } else {
        1
    }
    val clipSpec = animatedImageSpec.clipSpec?.lottieClipSpec

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(animatedImageSpec.resource.rawRes)
    )

    if (iterations == 1) {
        val animatable = rememberLottieAnimatable()
        LaunchedEffect(key1 = animatedImageSpec.clipSpec, key2 = composition) {
//            Log.d("[LottieFixes], entered LE, startAnimation: ${animatedImageSpec.startAnimation}, animatable.isPlaying: ${animatable.isPlaying}, animatedImageSpec.clipSpec: ${animatedImageSpec.clipSpec}")
            if (animatedImageSpec.startAnimation) {
//                Log.d("[LottieFixes], entered LE, animate")
                animatedImageSpec.animationStarted()
                animatable.animate(
                    composition,
                    iterations = iterations,
                    speed = speed,
                    clipSpec = clipSpec,
                )
            } else if (!animatable.isPlaying) {
//                Log.d("[LottieFixes], entered LE, snapTo")
                animatable.snapTo(
                    composition,
                    progress = (animatedImageSpec.clipSpec as? AnimatedImageClipSpec.Progress)?.max
                        ?: 1f,
                )
            }
        }
        LottieAnimation(
            composition = composition,
            progress = {
//                Log.d("[LottieFixes] progress: ${animatable.progress}")
                animatedImageSpec.animationProgressUpdates(animatable.progress)
                if (animatable.progress == 1f && !animatable.isPlaying) {
                    animatedImageSpec.animationCompleted()
                }

                animatable.progress
            },
            modifier = modifier,
            contentScale = contentScale
        )
    } else {
        LottieAnimation(
            composition,
            modifier = modifier,
            contentScale = contentScale,
            speed = speed,
            iterations = iterations,
            clipSpec = clipSpec,
        )
    }
}