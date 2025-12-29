package wallapp.ui.content.explore

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import wallapp.content.state.explore.ExploreHeaderViewState
import wallapp.graphics.composeColor
import wallapp.pixel.clickable.clickable
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.render.Render
import wallapp.pixel.shape.clip

@Composable
fun ExploreHeader(
    render: Render,
    viewState: ExploreHeaderViewState,
    modifier: Modifier = Modifier,
) {
    val paddingDefault = render.defaultViewSpec.paddingDefault
    val viewSpec = viewState.viewSpec
    val height = viewSpec.height

    val containerColor = viewState.containerColor
    val searchIcon = viewState.searchIcon
    val searchLabel = viewState.searchLabel
    val searchOnClick = viewState.searchOnClick
    val buttonHorizontalPadding = paddingDefault * 2
    val buttonVerticalPadding = paddingDefault / 2

    val animatedHeight by animateDpAsState(
        targetValue = height,
        animationSpec = tween(durationMillis = 500),
    )

    val shapeSpec = viewState.containerShapeSpec

    Box(
        modifier = modifier.fillMaxWidth()
            .height(animatedHeight)
            .clip(shapeSpec, render.shapeClipper)
            .background(color = containerColor.composeColor),
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .clickable(render) { searchOnClick() }
                .padding(horizontal = buttonHorizontalPadding, vertical = buttonVerticalPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MenuItem(
                render = render,
                menuItem = searchIcon,
            )
            MenuItem(
                render = render,
                menuItem = searchLabel,
            )
        }
    }
}