package wallapp.pixel.toolbar

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.onebone.toolbar.CollapsingToolbarScope
import wallapp.pixel.animation.AnimatedSnapshot
import wallapp.pixel.animation.AnimatedViewSpec
import wallapp.pixel.animation.animate
import wallapp.pixel.animation.animateSnapshot
import wallapp.pixel.compose.conditional
import wallapp.pixel.render.Render

@Composable
fun CollapsingToolbarScope.CollapsingToolbarContent(
    render: Render,
    animatedViewSpec: AnimatedViewSpec,
    modifier: Modifier,
    progress: Float,
    content: @Composable (Modifier, AnimatedSnapshot) -> Unit,
) {
    val whenExpanded = animatedViewSpec.alignmentWhenExpanded
    val whenCollapsed = animatedViewSpec.alignmentWhenCollapsed
    val snapshot = animatedViewSpec.animate(render, progress)

    content.invoke(
        modifier
            .animateSnapshot(snapshot)
            .conditional(whenExpanded != null && whenCollapsed != null) {
                road(
                    whenCollapsed = whenCollapsed!!,
                    whenExpanded = whenExpanded!!,
                )
            },
        snapshot,
    )
}

