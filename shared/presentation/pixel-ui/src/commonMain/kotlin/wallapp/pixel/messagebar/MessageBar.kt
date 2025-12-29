package wallapp.pixel.messagebar

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import wallapp.pixel.message.MessageBarViewState
import wallapp.pixel.render.Render
import wallapp.pixel.view.View

@Composable
fun MessageBar(
    render: Render,
    viewState: MessageBarViewState?,
    modifier: Modifier = Modifier,
) {
    val content = viewState?.content

    val isShow = remember(viewState) {
        viewState != null
    }

    // Animating alpha value
    val alphaValue = animateFloatAsState(
        targetValue = if (isShow) 1f else 0f,
        animationSpec = tween(durationMillis = 500),
    )

    // Animating Y offset
    val offsetY = animateFloatAsState(
        targetValue = if (isShow) 0f else (-100).toFloat(),
        animationSpec = tween(durationMillis = 500),
    )

    // Applying animated modifiers
    val animatedModifier =
        modifier.then(Modifier.offset(y = offsetY.value.dp)).then(Modifier.alpha(alphaValue.value))

    content?.let {
        View(render, view = it, modifier = animatedModifier)
    }
}
