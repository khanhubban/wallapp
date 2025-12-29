package wallapp.ui.content.home

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import wallapp.content.state.home.HomeTopBarViewState
import wallapp.graphics.composeColor
import wallapp.pixel.messagebar.MessageBar
import wallapp.pixel.render.Render
import wallapp.pixel.tab.Tabs
import wallapp.ui.content.profile.ProfileImage

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeTopBar(
    render: Render,
    viewState: HomeTopBarViewState,
    tabsPagerState: PagerState,
    modifier: Modifier = Modifier,
    messageBarHeight: Dp,
) {
    val viewSpec = viewState.viewSpec
    val height = viewSpec.height + messageBarHeight
    val profileVerticalPadding = viewSpec.profileVerticalPadding

    val profileImage = viewState.profileImage
    val tabs = viewState.tabs
    val containerColor = viewState.containerColor.composeColor

    val messageBar = viewState.messageBar

    val animatedHeight by animateDpAsState(
        targetValue = height,
        animationSpec = tween(durationMillis = 500),
    )

    val animatedMessageBarHeight by animateDpAsState(
        targetValue = messageBarHeight,
        animationSpec = tween(durationMillis = 500),
    )

    Box(
        modifier = modifier.fillMaxWidth().background(containerColor)
            .height(animatedHeight.minus(viewSpec.tabContainerHeight))
    )

    Box(
        modifier = modifier.fillMaxWidth()
            .height(animatedHeight),
    ) {

        MessageBar(render, messageBar, modifier = Modifier.height(messageBarHeight))

        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(top = animatedMessageBarHeight),
        ) {

            Spacer(modifier = Modifier.height(profileVerticalPadding))
            ProfileImage(
                render = render,
                profileImage = profileImage,
                eventHandler = profileImage.eventHandler,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
            Spacer(modifier = Modifier.height(profileVerticalPadding))
            Tabs(
                render = render,
                tabs = tabs,
                pagerState = tabsPagerState,
            )
        }
    }
}
