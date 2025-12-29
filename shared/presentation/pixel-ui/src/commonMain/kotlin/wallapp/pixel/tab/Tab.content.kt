package wallapp.pixel.tab


import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.focusable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import wallapp.pixel.clickable.clickable
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.render.Render
import wallapp.pixel.theme.ThemeColorTypeMapper

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Tab(
    render: Render,
    tab: TabViewState,
    index: Int,
    pagerState: PagerState,
    tabHeight: Dp = 56.dp,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val selectedContentColor = tab.selectedContentColorToken?.let { ThemeColorTypeMapper.map(it) }
        ?: LocalContentColor.current
    val selected = index == pagerState.currentPage
    val header = if (selected) tab.headerSelected else tab.headerUnselected

    Tab(
        render = render,
        interactionSource = MutableInteractionSource(),
        selected = selected,
        onClick = {
            scope.launch {
                pagerState.animateScrollToPage(index)
            }
        },
        selectedContentColor = selectedContentColor,
        modifier = modifier
//            .background(MaterialTheme.colorScheme.background),
            .height(tabHeight),
    ) {
        Box(
            modifier = Modifier.height(tabHeight),
            contentAlignment = Alignment.Center,
        ) {
            MenuItem(render, menuItem = header)
        }
    }
}

/**
 * Custom [TabViewState] override to support custom clickable effect.
 */
@Composable
private fun Tab(
    render: Render,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    selectedContentColor: Color = LocalContentColor.current,
    unselectedContentColor: Color = selectedContentColor,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable ColumnScope.() -> Unit
) {

    TabTransition(selectedContentColor, unselectedContentColor, selected) {
        Column(
            modifier = modifier
                .hoverable(enabled = enabled, interactionSource = interactionSource)
                .clickable(render, onClick)
                .focusable(enabled = enabled, interactionSource = interactionSource).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            content = content
        )
    }
}

private const val TabFadeInAnimationDuration = 150
private const val TabFadeInAnimationDelay = 100
private const val TabFadeOutAnimationDuration = 100

@Composable
private fun TabTransition(
    activeColor: Color,
    inactiveColor: Color,
    selected: Boolean,
    content: @Composable () -> Unit
) {
    val transition = updateTransition(selected)
    val color by transition.animateColor(
        transitionSpec = {
            if (false isTransitioningTo true) {
                tween(
                    durationMillis = TabFadeInAnimationDuration,
                    delayMillis = TabFadeInAnimationDelay,
                    easing = LinearEasing
                )
            } else {
                tween(
                    durationMillis = TabFadeOutAnimationDuration,
                    easing = LinearEasing
                )
            }
        }
    ) {
        if (it) activeColor else inactiveColor
    }
    CompositionLocalProvider(
        LocalContentColor provides color,
        content = content
    )
}