package wallapp.pixel.animation

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import wallapp.unit.Padding

@Stable
data class AnimatedSnapshot(
    val progress: Float,
    val height: Dp? = null,
    val width: Dp? = null,
    val padding: Padding? = null,
    val alpha: Float? = null,
    val shape: Shape? = null,
)