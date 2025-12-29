package wallapp.ui.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import me.onebone.toolbar.CollapsingToolbarScope
import wallapp.pixel.animation.AnimatedViewSpec
import wallapp.pixel.compose.paddingAx
import wallapp.pixel.render.Render
import wallapp.pixel.toolbar.CollapsingToolbarContent
import wallapp.unit.Padding

@Composable
fun CollapsingToolbarScope.Separator(
    render: Render,
    progress: Float,
    animatedViewSpec: AnimatedViewSpec,
    thickness: Dp = 1.dp,
    padding: Padding = Padding(),
    modifier: Modifier = Modifier,
) {
    CollapsingToolbarContent(
        render,
        modifier = modifier,
        animatedViewSpec = animatedViewSpec,
        progress = progress,
    ) { mod, _ ->
        Box(
            modifier = mod,
        ) {
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .paddingAx(padding)
                    .align(Alignment.BottomCenter),
                thickness = thickness,
            )
        }
    }
}