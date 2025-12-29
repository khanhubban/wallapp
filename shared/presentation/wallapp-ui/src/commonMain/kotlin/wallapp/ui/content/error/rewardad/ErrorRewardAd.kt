package wallapp.ui.content.error.rewardad

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import wallapp.content.state.error.rewardad.ErrorRewardAdViewState
import wallapp.pixel.compose.statusBarsPadding
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.render.Render
import wallapp.pixel.theme.AppTheme
import wallapp.pixel.theme.dynamicColorScheme
import wallapp.ui.content.loading.ContentWithLoading
import wallapp.ui.content.toolbar.ToolbarOffset
import wallapp.ui.content.upgrade.plus.promo.UpgradePromo

@Composable
fun ErrorRewardAd(
    render: Render,
    viewState: ErrorRewardAdViewState,
    modifier: Modifier = Modifier,
) {
    ContentWithLoading(
        render,
        loadingIsVisible = viewState is ErrorRewardAdViewState.Loading,
        modifier = modifier,
    ) { contentModifier ->
        when (viewState) {
            ErrorRewardAdViewState.Loading -> {}

            is ErrorRewardAdViewState.Success -> {
                ErrorRewardAd(
                    render,
                    viewState,
                    contentModifier,
                )
            }
        }
    }
}

@Composable
fun ErrorRewardAd(
    render: Render,
    viewState: ErrorRewardAdViewState.Success,
    modifier: Modifier = Modifier,
) {
    val viewSpec = viewState.viewSpec
    val theme = viewState.theme
    val colorScheme = dynamicColorScheme(themeColors = theme.themeColors)

    AppTheme(
        render = render,
        colorScheme = colorScheme,
    ) {
        Surface(modifier) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .statusBarsPadding(render.windowFrame),
            ) {
                ErrorRewardAdContent(
                    render,
                    viewState,
                    modifier = Modifier.size(viewSpec.unlockContainerSize),
                )

                UpgradePromo(
                    render,
                    viewState.upgradePromoViewState,
                    modifier = Modifier.size(viewSpec.upgradeContainerSize),
                )
            }
        }
    }
}


@Composable
fun ErrorRewardAdContent(
    render: Render,
    viewState: ErrorRewardAdViewState.Success,
    modifier: Modifier = Modifier,
) {
    val paddingSmall = render.defaultViewSpec.paddingSmall
    val paddingDefault = render.defaultViewSpec.paddingDefault
    val messagePadding = paddingDefault / 3
    val unlockWallpaperContentPadding = viewState.viewSpec.unlockWallpaperContentPadding

    val toolbarViewState = viewState.toolbarViewState
    val messages = viewState.messages
    val errorMessage = viewState.errorMessage
    val actionButton = viewState.actionButton

    Column(
        modifier = modifier,
    ) {
        ToolbarOffset(
            render,
            toolbarViewState,
            offsetForStatusBar = false,
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = unlockWallpaperContentPadding,
                    bottom = unlockWallpaperContentPadding,
                    end = unlockWallpaperContentPadding,
                ),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Column {
                messages.forEachIndexed { index, menuItem ->
                    MenuItem(render, menuItem, Modifier.fillMaxWidth())
                    if (index != messages.size - 1) {
                        Spacer(modifier = Modifier.height(messagePadding))
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            errorMessage?.also {
                MenuItem(
                    render,
                    it,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = paddingSmall)
                        .alpha(.6f),
                )
            }
            MenuItem(render, actionButton, Modifier.fillMaxWidth())
        }
    }
}

