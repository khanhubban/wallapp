package wallapp.pixel.animation

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.lerp
import wallapp.math.lerpF
import wallapp.pixel.compose.ifNonNull
import wallapp.pixel.compose.lerp
import wallapp.pixel.compose.paddingAx
import wallapp.pixel.render.Render
import wallapp.pixel.render.shapeMapperComposable
import wallapp.unit.lerp

@Composable
fun AnimatedViewSpec.animate(
    render: Render,
    progress: Float,
    density: Density = LocalDensity.current,
): AnimatedSnapshot {
    val shapeMapper = render.shapeMapperComposable

    val width = if (widthMin != null && widthMax != null) {
        lerp(widthMin!!.dp, widthMax!!.dp, progress)
    } else {
        null
    }

    val height = if (heightMin != null && heightMax != null) {
        lerp(heightMin!!.dp, heightMax!!.dp, progress)
    } else {
        null
    }

    val alpha = if (alphaMin != null && alphaMax != null) {
        lerpF(alphaMin!!, alphaMax!!, progress)
    } else {
        null
    }

    val padding = lerp(paddingMin, paddingMax, progress)

    val shapeMin = shapeSpecMin?.let { shapeMapper.map(it) }
    val shapeMax = shapeSpecMax?.let { shapeMapper.map(it) }
    val shape = if (shapeMin != null && shapeMax != null) {
        lerp(shapeMin, shapeMax, progress, density)
    } else {
        null
    }

    return AnimatedSnapshot(
        progress = progress,
        width = width,
        height = height,
        padding = padding,
        alpha = alpha,
        shape = shape,
    )
}

fun Modifier.animateSnapshot(snapshot: AnimatedSnapshot): Modifier = this
    .ifNonNull(snapshot.padding) { paddingAx(it) }
    .ifNonNull(snapshot.width) { width(it) }
    .ifNonNull(snapshot.height) { height(it) }
    .ifNonNull(snapshot.alpha) { graphicsLayer(alpha = it) }
    .ifNonNull(snapshot.shape) { clip(it) }
