package wallapp.pixel.checkbox

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import wallapp.pixel.clickable.clickable
import wallapp.pixel.render.Render

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicCheckbox(
    render: Render,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    size: Int = 24,
    applyPadding: Boolean = true,
) {
    val colorScheme = MaterialTheme.colorScheme
    val color = colorScheme.onBackground

    val borderColor = color
    val backgroundColor by animateColorAsState(if (checked) color else Color.Transparent)

    CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides applyPadding) {
        Box(
            modifier = modifier
                .size(size.dp)
                .clip(shape)
                .clickable(render) { onCheckedChange(!checked) }
                .background(backgroundColor)
                .border(
                    BorderStroke(
                        width = 2.dp,
                        color = borderColor
                    ),
                    shape = shape
                ),
            contentAlignment = Alignment.Center,
        ) {
        }
    }
}
