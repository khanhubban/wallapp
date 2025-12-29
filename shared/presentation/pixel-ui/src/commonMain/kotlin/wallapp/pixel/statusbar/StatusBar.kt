package wallapp.pixel.statusbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import wallapp.pixel.render.Render
import wallapp.pixel.theme.ThemeColorTypeMapper
import wallapp.pixel.toolbar.ToolbarViewState

@Composable
fun StatusBar(
    render: Render,
    toolbar: ToolbarViewState,
    modifier: Modifier = Modifier,
) {
    val statusBarColor = ThemeColorTypeMapper.map(toolbar.containerColorOverride)

    StatusBar(render, modifier, color = statusBarColor)
}

@Composable
fun StatusBar(
    render: Render,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surfaceVariant,
    alpha: Float = 1f,
) {
    val statusBarHeight = render.windowFrame.statusBarHeight
    Box(
        modifier = modifier
            .height(statusBarHeight)
            .fillMaxWidth()
            .alpha(alpha)
            .background(color),
    )
}