package wallapp.ui.content.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.hazeChild
import wallapp.content.state.search.SearchResultsHeaderViewSpec
import wallapp.content.state.search.SearchResultsHeaderViewState
import wallapp.image.Image
import wallapp.pixel.clickable.clickable
import wallapp.pixel.compose.ifNonNull
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.render.Render
import wallapp.pixel.shape.ShapeSpec
import wallapp.pixel.shape.clip
import wallapp.pixel.text.Text
import wallapp.pixel.theme.ThemeColorTypeMapper
import wallapp.pixel.view.onClick

@Composable
fun SearchResultsHeader(
    render: Render,
    viewState: SearchResultsHeaderViewState,
    modifier: Modifier = Modifier,
    hazeState: HazeState? = null
) {
    val topScrim = viewState.topScrim
    val viewSpec = viewState.viewSpec
    val headerHeight = viewSpec.headerHeight
    val eventHandler = viewState.clickEventHandler
    val paddingSmall = render.defaultViewSpec.paddingSmall

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(headerHeight)
            .padding(bottom = paddingSmall)
    ) {
        HazeBlurLayer(
            hazeState,
            modifier = Modifier.fillMaxSize()
        )

        // Top Scrim
        Image(
            render = render,
            image = topScrim,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds,
        )

        val searchBarHeight = viewSpec.searchBarHeight
        val containerColor = ThemeColorTypeMapper.map(viewState.searchContainerColor)
        val searchBarShapeSpec = viewSpec.searchBarShapeSpec
        val horizontalExternalPadding = viewSpec.searchBarExternalHorizontalPadding

        var animateScrollRecipeBinToStart by remember { mutableStateOf(false) }
        val onClick = {
            animateScrollRecipeBinToStart = !animateScrollRecipeBinToStart
            eventHandler.onClick.invoke()
        }
        Box(
            modifier = Modifier
                .padding(top = viewSpec.barTopPadding)
                .fillMaxWidth()
                .height(searchBarHeight)
                .align(Alignment.BottomCenter)
                .padding(horizontal = horizontalExternalPadding)
                .clickable(render, onClick)
        ) {
            val recipeBinState = viewState.searchRecipeBinViewState
            if (recipeBinState != null) {
                SearchRecipeBinBar(
                    render = render,
                    viewState = recipeBinState,
                    viewSpec = recipeBinState.viewSpec,
                    containerColor = containerColor,
                    animateScrollToStart = animateScrollRecipeBinToStart,
                )
            } else {
                SearchResultsBar(
                    render = render,
                    viewState = viewState,
                    viewSpec = viewSpec,
                    shapeSpec = searchBarShapeSpec,
                    containerColor = containerColor,
                )
            }
        }
    }
}

@Composable
fun HazeBlurLayer(
    hazeState: HazeState?,
    blurRadius: Dp = 2.dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .ifNonNull(hazeState) {
                hazeChild(
                    state = hazeState!!,
                    shape = RoundedCornerShape(
                        bottomStartPercent = 25,
                        bottomEndPercent = 25,
                    ),
                    style = HazeStyle(
                        tint = Color.Transparent.copy(alpha = 0f),
                        blurRadius = blurRadius,
                    )
                )
            },
    )
}

@Composable
fun SearchResultsBar(
    render: Render,
    viewState: SearchResultsHeaderViewState,
    viewSpec: SearchResultsHeaderViewSpec,
    shapeSpec: ShapeSpec,
    containerColor: Color,
    modifier: Modifier = Modifier
) {
    val searchText = viewState.searchText
    val clearIcon = viewState.clearIcon
    val horizontalInternalPadding = viewSpec.searchBarInternalHorizontalPadding
    // Search bar
    Row(
        modifier = modifier
            .fillMaxSize()
            .clip(shapeSpec, render.shapeClipper)
            .background(containerColor)
            .padding(horizontal = horizontalInternalPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = searchText)
        Spacer(modifier = Modifier.weight(1f))
        MenuItem(
            render = render,
            menuItem = clearIcon,
        )
    }
}