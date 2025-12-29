package wallapp.ui.content.content

import androidx.compose.foundation.border
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp


fun Modifier.contentBorder(shape: Shape): Modifier =
    composed { this.border(1.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = .5f), shape) }

fun Modifier.contentContainer(shape: Shape): Modifier = clip(shape).contentBorder(shape)