// Copyright 2019, Google LLC, Christopher Banes and the Tivi project contributors
// SPDX-License-Identifier: Apache-2.0

package wallapp.pixel.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import wallapp.pixel.render.Render
import kotlin.math.pow

/**
 * Draws a vertical gradient scrim in the foreground.
 *
 * @param color The color of the gradient scrim.
 * @param decay The exponential decay to apply to the gradient. Defaults to `3.0f` which is
 * a cubic decay.
 * @param numStops The number of color stops to draw in the gradient. Higher numbers result in
 * the higher visual quality at the cost of draw performance. Defaults to `16`.
 */
fun Modifier.drawForegroundGradientScrim(
    color: Color,
    decay: Float = 3.0f,
    numStops: Int = 16,
    startY: Float = 0f,
    endY: Float = 1f,
): Modifier = composed {
    val colors = remember(color, numStops) {
        val baseAlpha = color.alpha
        List(numStops) { i ->
            val x = i * 1f / (numStops - 1)
            val opacity = x.pow(decay)
            color.copy(alpha = baseAlpha * opacity)
        }
    }

    drawWithContent {
        drawContent()
        drawRect(
            topLeft = Offset(x = 0f, y = startY * size.height),
            size = size.copy(height = (endY - startY) * size.height),
            brush = Brush.verticalGradient(colors = colors),
        )
    }
}


fun Modifier.backgroundGradientScrim(
    color: Color,
): Modifier {
    val brush = Brush.verticalGradient(
        0f to Color.Transparent,
        .85f to color,
        1f to color,
    )

    return background(brush = brush)
}

@Composable
fun BackgroundGradientScrim(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surface,
) {
    Box(
        modifier = modifier
            .backgroundGradientScrim(color = color)
    ) {
    }
}


@Composable
fun BoxWithFooterScrim(
    render: Render,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surface,
    scrimHeight: Dp = render.defaultViewSpec.screenScrimHeight,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
    ) {
        content()

        BackgroundGradientScrim(
            modifier = Modifier
                .fillMaxWidth()
                .height(scrimHeight)
                .align(Alignment.BottomCenter),
            color = color,
        )
    }
}