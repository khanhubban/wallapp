package wallapp.ui.content.loading

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import wallapp.pixel.render.Render

@Composable
fun ContentWithLoading(
    render: Render,
    loadingIsVisible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.(Modifier) -> Unit,
) {
    Box(modifier = modifier) {
        this.content(Modifier.matchParentSize())

        AnimatedLoadingVisibility(render, loadingIsVisible)
    }
}

@Composable
private fun AnimatedLoadingVisibility(
    render: Render,
    loadingIsVisible: Boolean,
) {
    val animationDuration = 400
    AnimatedVisibility(
        visible = loadingIsVisible,
        enter = fadeIn(animationSpec = tween(durationMillis = animationDuration)),
        exit = fadeOut(animationSpec = tween(durationMillis = animationDuration))
    ) {
        LoadingScreen(render, Modifier.fillMaxSize())
    }
}
