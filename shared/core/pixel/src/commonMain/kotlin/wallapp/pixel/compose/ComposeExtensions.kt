package wallapp.pixel.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.StateFlow
import wallapp.pixel.clickable.clickable
import wallapp.pixel.image.ImageViewSpec
import wallapp.pixel.image.ImageViewState
import wallapp.pixel.render.Render
import wallapp.pixel.util.GloballyPositionedListener
import wallapp.system.window.WindowFrame
import wallapp.unit.Dimension
import wallapp.unit.Padding
import wallapp.math.lerp as lerpf

@Composable
fun <T> StateFlow<T>.collectAsState(): State<T> = collectAsState(useLifecycleIfAvailable = true)

@Composable
expect fun <T> StateFlow<T>.collectAsState(useLifecycleIfAvailable: Boolean): State<T>

@Composable
fun Dp.dpToPx() = with(LocalDensity.current) { this@dpToPx.toPx() }

@Composable
fun Int.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }

expect fun Modifier.statusBarsPadding(windowFrame: WindowFrame): Modifier

expect fun Modifier.navigationBarsPadding(windowFrame: WindowFrame) : Modifier

fun Modifier.paddingAx(
    padding: Padding?,
): Modifier {
    if (padding == null) return this
    return padding(
        start = padding.start,
        top = padding.top,
        end = padding.end,
        bottom = padding.bottom,
    )
}

fun <T> Modifier.ifNonNull(
    value : T?,
    modifier : Modifier.(T) -> Modifier,
) : Modifier {
    return if (value != null) {
        then(modifier(Modifier, value))
    } else {
        this
    }
}

fun Modifier.conditional(
    condition: Boolean,
    modifier: Modifier.() -> Modifier,
) : Modifier {
    return if (condition) {
        then(modifier(Modifier))
    } else {
        this
    }
}

@Composable
fun Modifier.conditionalComposable(
    condition: Boolean,
    modifier: @Composable Modifier.() -> Modifier,
) : Modifier {
    return if (condition) {
        then(modifier(Modifier))
    } else {
        this
    }
}


fun Modifier.conditional(
    condition: Boolean,
    ifTrue: Modifier.() -> Modifier,
    ifFalse: (Modifier.() -> Modifier)? = null,
): Modifier {
    return if (condition) {
        then(ifTrue(Modifier))
    } else if (ifFalse != null) {
        then(ifFalse(Modifier))
    } else {
        this
    }
}

fun Modifier.clickableIfNotNull(
    render: Render,
    onClick: (() -> Unit)?,
): Modifier = if (onClick != null) {
    clickable(render) { onClick() }
} else {
    this
}

fun Modifier.clickableNoRippleIfNotNull(
    onClick: (() -> Unit)?,
): Modifier = if (onClick != null) {
    clickableNoRipple {
        onClick()
    }
} else {
    this
}

fun Modifier.clipIfNotNull(
    shape: Shape?,
): Modifier = if (shape != null) {
    clip(shape)
} else {
    this
}

fun Modifier.onGloballyPositioned(listener: GloballyPositionedListener?): Modifier =
    if (listener != null) {
        onGloballyPositioned {
            listener.onPositioned(it)
        }
    } else {
        this
    }

fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier = composed {
    clickable(indication = null,
        interactionSource = remember { MutableInteractionSource() }) {
        onClick()
    }
}

fun Modifier.clickableUnboundRipple(onClick: () -> Unit): Modifier = composed {
    clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = ripple(bounded = false)
    ) {
        onClick()
    }
}
fun Modifier.size(imageViewState: ImageViewState): Modifier {
    return this.size(imageViewState.viewSpec)
}

fun Modifier.size(imageViewSpec: ImageViewSpec): Modifier {
    return this.size(width = imageViewSpec.width, height = imageViewSpec.height)
}

fun lerp(start: CornerSize, stop: CornerSize, amount: Float, density: Density): CornerSize {
    return CornerSize(
        lerpf(
            start.toPx(Size.Unspecified, density),
            stop.toPx(Size.Unspecified, density),
            amount,
        )
    )
}

@Composable
fun Shape.scale(
    amount: Float,
    density: Density = LocalDensity.current,
): Shape {
    return when (this) {
        is CutCornerShape -> {
            lerp(CutCornerShape(0.dp), this, amount, density = density)
        }

        is RoundedCornerShape -> {
            lerp(RoundedCornerShape(0.dp), this, amount, density = density)
        }

        else -> {
            this
        }
    }
}

fun lerp(start: Shape, stop: Shape, amount: Float, density: Density): Shape {
    require(start::class == stop::class)
    require(start is CornerBasedShape)
    require(stop is CornerBasedShape)

    val topStart = lerp(start.topStart, stop.topStart, amount, density)
    val topEnd = lerp(start.topEnd, stop.topEnd, amount, density)
    val bottomStart = lerp(start.bottomStart, stop.bottomStart, amount, density)
    val bottomEnd = lerp(start.bottomEnd, stop.bottomEnd, amount, density)

    return when (start) {
        is RoundedCornerShape -> { RoundedCornerShape(topStart, topEnd, bottomEnd, bottomStart) }
        is CutCornerShape -> { CutCornerShape(topStart, topEnd, bottomEnd, bottomStart) }
        else -> {
            TODO("Add support for ${start::class.simpleName}")
        }
    }
}

val Shape.noBottomCorners: Shape
    get() {
        val shape = this
        require(shape is CornerBasedShape)
        val noCornerSize = CornerSize(0.dp)

        return when (shape) {
            is RoundedCornerShape -> { RoundedCornerShape(shape.topStart, shape.topEnd, noCornerSize, noCornerSize) }
            is CutCornerShape -> { CutCornerShape(shape.topStart, shape.topEnd, noCornerSize, noCornerSize) }
            else -> { TODO("Add support for ${shape::class.simpleName}") }
        }
    }

val Dimension?.px: Int?
    get() = if (this is Dimension.Pixels) { this.px } else { null }


val FullWidthDp: Dp
    get() = (-1).dp