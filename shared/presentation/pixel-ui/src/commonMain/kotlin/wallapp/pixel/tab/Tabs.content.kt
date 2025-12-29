package wallapp.pixel.tab

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import wallapp.pixel.compose.clickableNoRipple
import wallapp.pixel.compose.ifNonNull
import wallapp.pixel.compose.onGloballyPositioned
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.render.Render
import wallapp.pixel.theme.ThemeColorTypeMapper

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Tabs(
    render: Render,
    tabs: TabsViewState,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
) {
    when (tabs) {
        is TabsViewState.Indicator -> Tabs(render, tabs, pagerState, modifier)
        is TabsViewState.Pill -> PillTabs(render, tabs, pagerState, modifier)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Tabs(
    render: Render,
    tabs: TabsViewState.Indicator,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
) {
    val selectedTabIndex = pagerState.currentPage
    val selectedContentColor = tabs.indicatorColorToken?.let { ThemeColorTypeMapper.map(it) }
        ?: MaterialTheme.colorScheme.primary
    val containerColor = tabs.containerColorToken?.let { ThemeColorTypeMapper.map(it) }
        ?: MaterialTheme.colorScheme.surfaceVariant
    val paddingDefault = render.defaultViewSpec.paddingDefault
    val tabContainerHeight = tabs.viewSpec.tabContainerHeight
    val indicatorHeight = tabs.viewSpec.tabIndicatorHeight
    val tabHeight = tabs.viewSpec.tabHeight
    val cachedTabPositions = remember { mutableStateOf<List<TabPosition>?>(null) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(tabContainerHeight),
    ) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = modifier
                .height(tabHeight),
            containerColor = containerColor,
            indicator = @Composable { tabPositions ->
                cachedTabPositions.value = tabPositions
            },
            divider = {},
        ) {
            tabs.tabs.forEachIndexed { index, tab ->
                Tab(render, tab, index, pagerState, tabHeight)
            }
        }
        val tabPositions = cachedTabPositions.value
        if (tabPositions != null) {
            TabIndicator(
                color = selectedContentColor,
                tabPosition = tabPositions[selectedTabIndex],
                padding = paddingDefault,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(indicatorHeight)
                    .background(Color.Transparent)
            )
        }
    }
}

@Composable
fun TabIndicator(
    color: Color,
    tabPosition: TabPosition,
    padding: Dp,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .tabIndicatorOffset(tabPosition)
            .fillMaxSize()
            .padding(horizontal = padding)
            .background(color),
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PillTabs(
    render: Render,
    tabs: TabsViewState.Pill,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
) {
    val selectedTabIndex = pagerState.currentPage

    val selectedContentColor = tabs.indicatorColorToken?.let { ThemeColorTypeMapper.map(it) }
        ?: MaterialTheme.colorScheme.primary
    val containerColor = tabs.containerColorToken?.let { ThemeColorTypeMapper.map(it) }
        ?: MaterialTheme.colorScheme.surfaceVariant

    val containerHeight = tabs.viewSpec.containerHeight
    val containerWidth = tabs.viewSpec.tabWidth

    val tabPositions = remember { mutableStateListOf<MyTabPosition>() }

    Box(
        modifier = modifier
            .height(containerHeight)
            .ifNonNull(containerWidth) { width(it.dp) }
            .background(color = containerColor, shape = CircleShape)
    ) {
        // Capsule Indicator
        if (tabPositions.size == tabs.size) {
            val currentTabPosition = tabPositions[selectedTabIndex]
            val indicatorOffset by animateFloatAsState(
                targetValue = currentTabPosition.left,
                animationSpec = tween(durationMillis = 300)
            )
            val indicatorWidth by animateFloatAsState(
                targetValue = currentTabPosition.width,
                animationSpec = tween(durationMillis = 300)
            )

            Box(
                modifier = Modifier
                    .offset(x = indicatorOffset.toDp())
                    .width(indicatorWidth.toDp())
                    .height(containerHeight)
                    .padding(4.dp)
                    .background(selectedContentColor, shape = CircleShape)
                    .clip(CircleShape)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.tabs.forEachIndexed { index, tab ->
                PillTab(
                    render = render,
                    index = index,
                    tab = tab,
                    tabPositions = tabPositions,
                    selectedTabIndex = selectedTabIndex,
                    pagerState = pagerState,
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RowScope.PillTab(
    render: Render,
    index: Int,
    tab: TabViewState,
    tabPositions: SnapshotStateList<MyTabPosition>,
    selectedTabIndex: Int,
    pagerState: PagerState,
) {
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .onGloballyPositioned { coordinates ->
                val position = MyTabPosition(
                    left = coordinates.positionInParent().x,
                    width = coordinates.size.width.toFloat()
                )
                if (tabPositions.size > index) {
                    tabPositions[index] = position
                } else {
                    tabPositions.add(position)
                }
            }
            .clickableNoRipple {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(index)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        val isSelected = index == selectedTabIndex

        val targetAlpha by animateFloatAsState(
            targetValue = if (isSelected) 1f else 0f,
            animationSpec = tween(durationMillis = 300)
        )

        // Render both `headerSelected` and `headerUnselected` during the animation. Visually, this
        // makes the items animate in and out smoothly.
        Box {
            if (targetAlpha < 1f) {
                MenuItem(
                    render = render,
                    menuItem = tab.headerUnselected,
                    modifier = Modifier.alpha(1f - targetAlpha)
                )
            }
            if (targetAlpha > 0f) {
                MenuItem(
                    render = render,
                    menuItem = tab.headerSelected,
                    modifier = Modifier.alpha(targetAlpha)
                )
            }
        }
    }
}

data class MyTabPosition(val left: Float, val width: Float)
@Composable
fun Float.toDp(): Dp = with(LocalDensity.current) { this@toDp.toDp() }