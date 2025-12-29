package wallapp.ui.content.rewardadinternal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import wallapp.content.state.rewardadinternal.RewardAdInternalViewState
import wallapp.image.Image
import wallapp.pixel.clickable.clickable
import wallapp.pixel.compose.BackHandler
import wallapp.pixel.compose.clickableNoRipple
import wallapp.pixel.compose.navigationBarsPadding
import wallapp.pixel.compose.statusBarsPadding
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.render.Render
import wallapp.pixel.render.shapeMapperComposable
import wallapp.pixel.shape.ShapeSize
import wallapp.pixel.shape.ShapeSpec
import wallapp.pixel.shape.ShapeStyle
import wallapp.pixel.theme.AppTheme
import wallapp.pixel.theme.ThemeColorTypeMapper
import wallapp.pixel.theme.dynamicColorScheme
import wallapp.pixel.view.ViewEventHandler
import wallapp.pixel.view.onClick
import wallapp.theme.ColorToken
import wallapp.ui.content.loading.LoadingScreen

@Composable
fun RewardAdInternalScreen(
    render: Render,
    screenViewState: RewardAdInternalViewState,
    modifier: Modifier = Modifier,
) {
    when (screenViewState) {
        is RewardAdInternalViewState.Loading -> {
            LoadingScreen(
                render,
                modifier,
            )
        }
        is RewardAdInternalViewState.Data -> {
            RewardAdInternal(
                render,
                screenViewState,
                modifier,
            )
        }
    }
}

@Composable
private fun RewardAdInternal(
    render: Render,
    screenViewState: RewardAdInternalViewState.Data,
    modifier: Modifier = Modifier,
) {
    val themeColors = screenViewState.theme.themeColors
    val colorScheme = dynamicColorScheme(themeColors = themeColors)
    val statusBarShadow = screenViewState.statusBarShadow

    val onBack = screenViewState.onBack

    BackHandler {
        onBack()
    }

    AppTheme(
        render = render,
        colorScheme = colorScheme,
    ) {
        Box(
            modifier = modifier.fillMaxSize(),
        ) {
            RewardAdInternalBackground(
                render,
                screenViewState,
                modifier = Modifier.fillMaxSize(),
            )

            RewardAdInternalForeground(
                render,
                screenViewState,
                modifier = Modifier.fillMaxSize(),
            )

            Image(
                render = render,
                image = statusBarShadow,
                modifier = Modifier.height(render.windowFrame.statusBarHeight).fillMaxWidth(),
                contentScale = ContentScale.FillBounds,
            )

        }
    }
}

@Composable
private fun RewardAdInternalBackground(
    render: Render,
    screenViewState: RewardAdInternalViewState.Data,
    modifier: Modifier = Modifier,
) {
    val heroMedia = screenViewState.heroMedia
    val fallbackMedia = screenViewState.fallbackMedia
    Box(modifier = modifier) {
        Image(render, fallbackMedia, modifier = Modifier.fillMaxSize())
        if (heroMedia != null) {
            Image(render, heroMedia, modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun RewardAdInternalForeground(
    render: Render,
    screenViewState: RewardAdInternalViewState.Data,
    modifier: Modifier = Modifier,
) {
    val paddingDefault = render.defaultViewSpec.paddingDefault

    val onClickContent = screenViewState.onClickContent.onClick
    val closeButton = screenViewState.closeButton
    val rewardStatus = screenViewState.rewardStatus
    val rewardStatusViewEventHandler = screenViewState.rewardStatusViewEventHandler

    Box(
        modifier = modifier
            .statusBarsPadding(render.windowFrame)
            .clickable(render, onClickContent),
    ) {
        RewardPill(
            render,
            rewardStatus,
            closeButton,
            rewardStatusViewEventHandler,
            modifier = Modifier
                .padding(paddingDefault)
                .align(Alignment.TopEnd)
        )

        RewardAdInternalForegroundFooter(
            render,
            screenViewState,
        )
    }
}

@Composable
private fun BoxScope.RewardAdInternalForegroundFooter(
    render: Render,
    screenViewState: RewardAdInternalViewState.Data,
    modifier: Modifier = Modifier,
) {
    val paddingSmall = render.defaultViewSpec.paddingSmall
    val paddingDefault = render.defaultViewSpec.paddingDefault

    val callToAction = screenViewState.callToAction
    val disclaimer = screenViewState.disclaimer
    val footerShadow = screenViewState.footerShadow
    val footerHeight = 128.dp

    Box(
        modifier = modifier
            .align(Alignment.BottomCenter)
            .height(footerHeight)
            .fillMaxWidth(),
    ) {
        Image(
            render,
            footerShadow,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .rotate(180f)
                .fillMaxSize(),
        )
    }
    Column(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(horizontal = paddingDefault)
            .navigationBarsPadding(render.windowFrame)
            .padding(bottom = paddingDefault),
    ) {
        MenuItem(render, callToAction)
        Spacer(modifier = Modifier.padding(paddingSmall))
        MenuItem(render, disclaimer)
    }
}

@Composable
private fun RewardPill(
    render: Render,
    rewardStatus: MenuItem,
    closeButton: MenuItem,
    viewEventHandler: ViewEventHandler,
    modifier: Modifier = Modifier
) {
    val paddingSmall = render.defaultViewSpec.paddingSmall
    val onClick = viewEventHandler.onClick
    val backgroundColor = ThemeColorTypeMapper.map(ColorToken.ThemeOnSurfaceVariant)
    val shape = render.shapeMapperComposable
        .map(ShapeSpec(ShapeStyle.RoundedCorners, ShapeSize.ExtraLarge))!!

    Row(
        modifier = modifier
            .background(color = backgroundColor, shape = shape)
            .clip(shape)
            .clickableNoRipple(onClick)
            .padding(render.defaultViewSpec.paddingSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.width(paddingSmall))
        MenuItem(render, rewardStatus)
        Spacer(modifier = Modifier.width(paddingSmall))
        MenuItem(render, closeButton, modifier = Modifier.alpha(0.6f))
    }
}