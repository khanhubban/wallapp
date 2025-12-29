package wallapp.ui.content.search

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.zIndex
import wallapp.content.state.search.SearchColorsViewState
import wallapp.content.state.search.SearchRecipeBinText
import wallapp.content.state.search.SearchRecipeBinViewSpec
import wallapp.content.state.search.SearchRecipeBinViewState
import wallapp.content.state.widget.EdgeFadeViewState
import wallapp.image.Image
import wallapp.pixel.compose.conditional
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.render.Render
import wallapp.pixel.shape.clip
import wallapp.pixel.spacer.SpacerViewState
import wallapp.pixel.text.Text
import wallapp.pixel.theme.ThemeColorTypeMapper
import wallapp.pixel.view.ViewEventHandler

@Composable
fun SearchRecipeBinBar(
    render: Render,
    viewState: SearchRecipeBinViewState,
    viewSpec: SearchRecipeBinViewSpec,
    visibilityProgress: Float = 1f,
    containerColor: Color = MaterialTheme.colorScheme.background,
    animateScrollToStart: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(viewState.shape, render.shapeClipper)
            .background(containerColor)
            .padding(start = viewSpec.startPadding, end = viewSpec.endPadding),
    ) {
        val scrollState = rememberScrollState()
        val recipeTextOpacity = viewSpec.recipeTextOpacity

        val clearIcon = viewState.clearIcon
        val checkIcon = viewState.checkIcon
        val actionIconSpacing = viewSpec.actionIconSpacing
        val edgeFade = viewState.edgeFade

        // Scrolling to start to match the two recipe bin bars when transitioning
        val animateToStartBasedOnVisibility by remember(visibilityProgress) {
            derivedStateOf {
                visibilityProgress < 1f
            }
        }
        LaunchedEffect(animateToStartBasedOnVisibility) {
            scrollState.animateScrollTo(0)
        }
        LaunchedEffect(animateScrollToStart) {
            scrollState.animateScrollTo(0)
        }

        val offsetX = viewState.endOffsetX
        val recipeEndPadding = viewSpec.recipeEndPadding

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = recipeEndPadding)
                .horizontalScroll(scrollState),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            viewState.viewStates.forEach { childState ->
                when (childState) {
                    is SearchColorsViewState -> {
                        SearchColors(
                            render = render,
                            viewState = childState,
                            modifier = Modifier
                                .width(childState.itemSize)
                        )
                    }
                    is SearchRecipeBinText -> {
                        childState.tags.forEach { text ->
                            Text(text = text, modifier = Modifier.alpha(recipeTextOpacity))
                        }
                    }
                    is SpacerViewState -> {
                        if (childState.width != null) {
                            Spacer(modifier = Modifier.width(childState.width!!.dp))
                        }
                    }
                }
            }
        }

        EdgeFade(
            render = render,
            edgeFade = edgeFade,
            overrideTintColor = containerColor,
        )

        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
        ) {

            MenuItem(
                render = render,
                menuItem = clearIcon,
                modifier = Modifier
                    .offset(x = offsetX)
                    .zIndex(1f)
            )
            if (checkIcon != null) {
                // For check icon animation
                var checkIconClicked by remember { mutableStateOf(false) }
                val modifiedCheckIcon = (checkIcon as? MenuItem.MenuItemButton)?.copy(
                    button = checkIcon.button.copy(
                        eventHandler = ViewEventHandler.createOnClick {
                            checkIconClicked = true
                            checkIcon.button.eventHandler?.invoke()
                        }
                    )
                )
                // add a scale effect such that the icon/button looks like its being pressed
                val scale = remember { Animatable(0f) }
                LaunchedEffect(checkIconClicked) {
                    if (checkIconClicked) {
                        scale.animateTo(
                            targetValue = if (checkIconClicked) 1f else 0f,
                            animationSpec = tween(
                                durationMillis = 150,
                                easing = FastOutSlowInEasing
                            )
                        )
                        checkIconClicked = false
                    } else {
                        scale.snapTo(0f)
                    }
                }

                // if scale is between 0 and 0.5, scaleEffect will go from 1 to 0.4
                // and if scale is between 0.5 and 1, scaleEffect will go from 0.4 to 1
                val scaleEffect = if (scale.value < 0.5f) {
                    1 - ((scale.value * 2) * 0.6f)
                } else {
                    0.4f + (((scale.value - 0.5f) * 2) * 0.6f)
                }

                Spacer(modifier = Modifier.width(actionIconSpacing))
                MenuItem(
                    render = render,
                    menuItem = modifiedCheckIcon ?: checkIcon,
                    modifier = Modifier
                        .scale(scaleEffect)
                )
            }
        }
    }
}

@Composable
fun BoxScope.EdgeFade(
    render: Render,
    edgeFade: EdgeFadeViewState,
    overrideTintColor: Color? = null,
    modifier: Modifier = Modifier,
) {
    val edgeImage = edgeFade.image
    val viewSpec = edgeFade.viewSpec
    val tintColor = overrideTintColor ?: ThemeColorTypeMapper.map(viewSpec.tintColorToken)
    val height = viewSpec.height
    val endOffsetX = viewSpec.endOffsetX
    Image(
        render = render,
        image = edgeImage,
        contentScale = ContentScale.FillBounds,
        colorFilter = ColorFilter.tint(tintColor),
        modifier = modifier
            .align(Alignment.CenterStart)
            .padding(start = viewSpec.startPadding)
            .width(viewSpec.startWidth)
            .conditional(height != null) {
                height(height!!.dp)
            }
    )
    Image(
        render = render,
        image = edgeImage,
        contentScale = ContentScale.FillBounds,
        colorFilter = ColorFilter.tint(tintColor),
        modifier = modifier
            .align(Alignment.CenterEnd)
            .padding(end = viewSpec.endPadding)
            .width(viewSpec.endWidth)
            .conditional(height != null) {
                height(height!!.dp)
            }
            .rotate(180f)
            .offset(x = endOffsetX)
    )
}