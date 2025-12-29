package wallapp.ui.content.collection

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.CollapsingToolbarScaffoldScope
import me.onebone.toolbar.CollapsingToolbarScaffoldState
import me.onebone.toolbar.CollapsingToolbarScope
import me.onebone.toolbar.ExperimentalToolbarApi
import me.onebone.toolbar.ScrollStrategy
import me.onebone.toolbar.SnapConfig
import wallapp.content.state.collection.CollectionToolbarViewState
import wallapp.math.clamp
import wallapp.pixel.animation.AnimatedViewSpec
import wallapp.pixel.feed.CollapsingToolbarAnimationDuration
import wallapp.pixel.render.Render
import wallapp.ui.content.toolbar.rememberCollapsingToolbarScaffoldPersistableState
import wallapp.ui.widget.Separator
import wallapp.unit.Padding


@OptIn(ExperimentalToolbarApi::class)
@Composable
fun CollectionCollapsingToolbarScaffold(
    render: Render,
    modifier: Modifier,
    toolbar: CollectionToolbarViewState,
    body: @Composable CollapsingToolbarScaffoldScope.() -> Unit
) {
    val collapsingToolbarState = rememberCollapsingToolbarScaffoldPersistableState(
        toolbar.collapsingToolbarStateWrapper.lastCollapsingToolbarState,
        toolbar.collapsingToolbarStateWrapper.lastCollapsingToolbarStateUpdateSink,
    )

    val toolbarExpanded = toolbar.isExpanded
    LaunchedEffect(toolbarExpanded) {
        if (toolbarExpanded) {
            collapsingToolbarState.expand(CollapsingToolbarAnimationDuration)
        } else {
            collapsingToolbarState.collapse(CollapsingToolbarAnimationDuration)
        }
    }

    CollapsingToolbarScaffold(
        modifier = modifier
            .fillMaxSize(),
        state = collapsingToolbarState,
        scrollStrategy = ScrollStrategy.ExitUntilCollapsed,
        enabled = false,
        snapConfig = SnapConfig(),
        toolbar = {
            CollapsingToolbar(
                render,
                toolbar,
                collapsingToolbarState,
            )
        }
    ) {
        body()
    }
}

fun getAlpha(progress: Float, invisibleStep: Float): Float {
    return if (progress < invisibleStep) {
        0f
    } else {
        clamp((progress - invisibleStep) / (1f - invisibleStep), 0f, 1f)
    }
}


@Composable
internal fun CollapsingToolbarScope.Separator(
    render: Render,
    progress: Float,
    viewSpec: AnimatedViewSpec,
    height: Dp,
    modifier: Modifier = Modifier,
    horizontalPadding: Dp = render.defaultViewSpec.paddingDefault,
) {
    Separator(
        render,
        progress,
        viewSpec,
        thickness = height,
        padding = Padding(horizontal = horizontalPadding),
        modifier,
    )
}

@Composable
private fun CollapsingToolbarScope.CollapsingToolbar(
    render: Render,
    toolbar: CollectionToolbarViewState,
    collapsingToolbarState: CollapsingToolbarScaffoldState,
) {
   when (toolbar) {
       is CollectionToolbarViewState.Unlocked -> {
           CollapsingToolbarUnlocked(
               render,
               toolbar,
               collapsingToolbarState,
           )
       }
       is CollectionToolbarViewState.Locked -> {
           CollapsingToolbarLocked(
               render,
               toolbar,
               collapsingToolbarState,
           )
       }
    }
}