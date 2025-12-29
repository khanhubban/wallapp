package wallapp.ui.content.favorite

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.max
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
import wallapp.content.state.favorite.FavoriteViewState
import wallapp.image.Image
import wallapp.image.lottieClipSpec
import wallapp.pixel.compose.clickableUnboundRipple
import wallapp.pixel.render.Render
import wallapp.pixel.theme.ThemeColorTypeMapper
import wallapp.pixel.view.onClick
import wallapp.resource.AnimatedImageClipSpec
import wallapp.resource.Resource
import wallapp.resource.rawRes

@Composable
actual fun AnimatedFavoriteButton(
    render: Render,
    viewState: FavoriteViewState,
    tintColor: Color?,
    modifier: Modifier,
) {
    val animatable = rememberLottieAnimatable()
    val image = if (viewState.isFavorite) {
        viewState.selectedImageAnimated
    } else {
        viewState.unselectedImageAnimated
    } as? Image.ImageResource ?: return

    val animatedImageSpec = (image.resource as? Resource.AnimatedImage)?.spec ?: return
    val speed = animatedImageSpec.speed
    val iterations = if (animatedImageSpec.infiniteRepeat) {
        LottieConstants.IterateForever
    } else {
        1
    }
    val animatedClipSpec = animatedImageSpec.clipSpec
    val clipSpec = animatedClipSpec?.lottieClipSpec
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(animatedImageSpec.resource.rawRes)
    )

    var currentFavoriteState: Boolean? by remember { mutableStateOf(null) }
    var currentFavoriteId: String? by remember { mutableStateOf(null) }
    var currentTintColor: Color? by remember { mutableStateOf(null) }

    // apply tint color
    currentTintColor = tintColor ?: ThemeColorTypeMapper.map(colorToken = viewState.tintColor)

    val dynamicProperties = key(currentTintColor) {
        if (currentTintColor != null) {
            rememberLottieDynamicProperties(
                rememberLottieDynamicProperty(
                    property = LottieProperty.COLOR_FILTER,
                    value = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                        currentTintColor!!.toArgb(),
                        BlendModeCompat.SRC_ATOP
                    ),
                    keyPath = arrayOf(
                        "**"
                    )
                )
            )
        } else {
            rememberLottieDynamicProperties()
        }
    }

//    Log.d("[AnimatedFavoriteButton] AnimatedFavoriteButton, viewState.isFavorite: ${viewState.isFavorite}, currentFavoriteState: $currentFavoriteState")

    LaunchedEffect(viewState.isFavorite, viewState.id) {
//        Log.d("[AnimatedFavoriteButton] LaunchedEffect1, viewState.isFavorite: ${viewState.isFavorite}, currentFavoriteState: $currentFavoriteState, animatable.isPlaying: ${animatable.isPlaying}")
        val startAnimation = currentFavoriteState != null
                && currentFavoriteState != viewState.isFavorite
                && currentFavoriteId == viewState.id
        if (startAnimation) {
            animatable.animate(
                composition,
                iterations = iterations,
                speed = speed,
                clipSpec = clipSpec,
            )
        } else {
            val snapToProgress = (animatedClipSpec as? AnimatedImageClipSpec.Progress)?.max ?: 1f
            animatable.snapTo(
                composition,
                progress = snapToProgress,
            )
        }
        currentFavoriteState = viewState.isFavorite
        currentFavoriteId = viewState.id
    }

    val iconButtonLayerSize = max(render.defaultViewSpec.iconButtonLayerSize, viewState.viewSpec.size)
    val onClick = {
        viewState.onClick.onClick()
    }

    Box(
        modifier
            .minimumInteractiveComponentSize()
            .size(iconButtonLayerSize)
            .clickableUnboundRipple(onClick),
    ) {
        LottieAnimation(
            composition = composition,
            progress = {
                animatable.progress
            },
            modifier = Modifier,
            contentScale = ContentScale.Fit,
            dynamicProperties = dynamicProperties,
        )
    }
}