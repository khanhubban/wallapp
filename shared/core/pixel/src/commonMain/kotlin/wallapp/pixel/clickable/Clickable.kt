package wallapp.pixel.clickable

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.datetime.Clock
import wallapp.log.Log
import wallapp.pixel.render.Render

private const val DefaultDebounceTime = 250L

fun Modifier.clickableAlphaDebounce(
    debounceTime: Long = DefaultDebounceTime,
    onClick: () -> Unit,
): Modifier = composed {
    var isPressed by remember { mutableStateOf(false) }
    var lastClickTime by remember { mutableStateOf(0L) }

    val animatedAlpha by animateFloatAsState(
        targetValue = if (isPressed) 0.67f else 1.0f,
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 120)
    )

    val pressModifier = pointerInput(Unit) {
        detectTapGestures(
            onPress = {
                isPressed = true
                tryAwaitRelease()
                isPressed = false
            },
            onTap = {
                val currentTime = Clock.System.now().toEpochMilliseconds()
                if (currentTime - lastClickTime > debounceTime) {
                    lastClickTime = currentTime
                    onClick()
                    Log.d("Clickable - onClick()")
                } else {
                    Log.i("Clickable - debounced click")
                }
            }
        )
    }

    this.then(pressModifier)
        .graphicsLayer(alpha = animatedAlpha)
}

fun Modifier.clickableRippleDebounce(
    debounceTime: Long = DefaultDebounceTime,
    onClick: () -> Unit
): Modifier {
    return this.composed {
        val clickable = debounced(debounceTime = debounceTime, onClick = { onClick() })
        this.clickable { clickable() }
    }
}

fun Modifier.clickable(
    render: Render,
    onClick: () -> Unit,
): Modifier {
    return clickable(render.config.clickableEffect, onClick)
}

fun Modifier.clickable(
    clickableEffect: ClickableEffect,
    onClick: () -> Unit,
): Modifier {
    return when (clickableEffect) {
        ClickableEffect.Ripple -> clickableRippleDebounce(onClick = onClick)
        ClickableEffect.AlphaFade -> clickableAlphaDebounce(onClick = onClick)
    }
}


@Composable
private inline fun debounced(crossinline onClick: () -> Unit, debounceTime: Long = DefaultDebounceTime): () -> Unit {
    var lastTimeClicked by remember { mutableStateOf(0L) }
    val onClickLambda: () -> Unit = {
        val now = Clock.System.now().toEpochMilliseconds()
        if (now - lastTimeClicked > debounceTime) {
            onClick()
            Log.d("Clickable - onClick()")
        } else {
            Log.i("Clickable - debounced click")
        }
        lastTimeClicked = now
    }
    return onClickLambda
}