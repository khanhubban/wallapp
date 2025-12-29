package wallapp.ui.content.paywall

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import wallapp.content.state.upgrade.PaywallViewEvent
import wallapp.content.state.upgrade.plus.paywall.PaywallFeatureViewState
import wallapp.content.state.upgrade.plus.paywall.PaywallViewState
import wallapp.image.Image
import wallapp.log.Log
import wallapp.pixel.compose.statusBarsPadding
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.render.Render
import wallapp.pixel.tab.TabbedContent
import wallapp.pixel.tab.rememberPersistablePagerState
import wallapp.pixel.text.Text
import wallapp.pixel.view.View
import wallapp.ui.content.error.ErrorScreen
import wallapp.ui.content.loading.ContentWithLoading

@Composable
fun Paywall(
    render: Render,
    viewState: PaywallViewState,
    modifier: Modifier = Modifier,
) {
    ContentWithLoading(
        render,
        loadingIsVisible = viewState is PaywallViewState.Loading,
        modifier = modifier,
    ) { contentModifier ->

        when (viewState) {
            is PaywallViewState.Loading -> {}

            is PaywallViewState.Native -> {
                Paywall(
                    render = render,
                    viewState = viewState,
                    modifier = contentModifier,
                )
            }

            is PaywallViewState.NativeTabs -> {
                Paywall(
                    render = render,
                    viewState = viewState,
                    modifier = contentModifier,
                )
            }

            is PaywallViewState.Error -> {
                ErrorScreen(
                    render = render,
                    viewState = viewState.errorViewState,
                    modifier = contentModifier,
                )
            }

            is PaywallViewState.Remote -> {
                Paywall(
                    render = render,
                    viewState = viewState,
                    modifier = contentModifier,
                )
            }
        }
    }
}

@Composable
fun Paywall(
    render: Render,
    viewState: PaywallViewState.Remote,
    modifier: Modifier = Modifier,
) {
    val paywallUiController = viewState.remotePaywallUiController
    val paywallEvent = viewState.remotePaywallEvent
    val fallback = viewState.fallback

    val errorComposable: @Composable ((Throwable) -> Unit) = {
        Log.e("Paywall - error: $it")
        Paywall(render, fallback, modifier)
    }

    Paywall(render, fallback, modifier)
    //Comment RevenueCat PaywallContent
    /*    PaywallContent(
            render = render,
            remotePaywallEvent = paywallEvent,
            remotePaywallUiController = paywallUiController,
            modifier = modifier,
            remotePaywallCallbacks = viewState.remotePaywallCallbacks,
            errorComposable = errorComposable,
        )*/
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Paywall(
    render: Render,
    viewState: PaywallViewState.Native,
    modifier: Modifier = Modifier,
) {
    val paddingDefault = render.defaultViewSpec.paddingDefault

    val actionButton = viewState.actionButton
    val title = viewState.subscriptionTitle
    val paywallPlanView = viewState.paywallPlanView

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black),
        ) {
            Image(
                render,
                viewState.featureImageViewState,
                modifier = Modifier.statusBarsPadding(render.windowFrame)
            )

            MenuItem(
                render,
                viewState.close,
                modifier = Modifier.statusBarsPadding(render.windowFrame)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = paddingDefault)
                .padding(bottom = render.windowFrame.navigationBarHeight + paddingDefault)
                .weight(1f),
        ) {
            Spacer(modifier = Modifier.height(paddingDefault))

            MenuItem(render, title, modifier = Modifier.fillMaxWidth())

            View(
                render,
                paywallPlanView,
                modifier = Modifier.fillMaxWidth()
                    .weight(1f),
            )

            Spacer(modifier = Modifier.height(paddingDefault))

            MenuItem(render, actionButton, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(render.defaultViewSpec.paddingSmall))

            MenuItem(render, viewState.footerItem, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun PaywallFeature(
    render: Render,
    viewState: PaywallFeatureViewState,
    modifier: Modifier = Modifier,
) {
    val icon = viewState.icon
    val label = viewState.label
    val paddingDefault = render.defaultViewSpec.paddingDefault

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MenuItem(render, icon)
        Spacer(modifier = Modifier.width(paddingDefault))
        Text(label, modifier = Modifier.weight(1f))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Paywall(
    render: Render,
    viewState: PaywallViewState.NativeTabs,
    modifier: Modifier = Modifier,
) {
    val tabs = viewState.planSelectionTabs
    val pagerState: PagerState = rememberPersistablePagerState(
        initialPage = tabs.initialIndex,
        lastPagerStateUpdateSink = viewState.planSelectionLastPagerStateUpdateSink,
    ) { tabs.size }

    val paddingDefault = render.defaultViewSpec.paddingDefault

    val actionButton = viewState.actionButton
    val title = viewState.subscriptionTitle
    val initialTabPage = viewState.initialTabPage

    LaunchedEffect(initialTabPage) {
        pagerState.scrollToPage(initialTabPage)
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            (viewState.planSelectionTabs.tabs[page].viewEvent as? PaywallViewEvent)?.let {
                viewState.paywallViewEventSink.invoke(it)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black),
        ) {
            Image(
                render,
                viewState.featureImageViewState,
            )

            MenuItem(
                render,
                viewState.close,
                modifier = Modifier.statusBarsPadding(render.windowFrame)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = render.windowFrame.navigationBarHeight + paddingDefault)
                .weight(1f),
        ) {
            Spacer(modifier = Modifier.height(paddingDefault))
            MenuItem(render, title, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(paddingDefault))
            TabbedContent(
                render = render,
                tabs = tabs,
                modifier = Modifier.fillMaxWidth()
                    .weight(1f),
                pagerState,
            )

            MenuItem(render, actionButton, modifier = Modifier.fillMaxWidth()
                .padding(horizontal = paddingDefault))

            Spacer(modifier = Modifier.height(render.defaultViewSpec.paddingSmall))

            MenuItem(render, viewState.footerItem, modifier = Modifier.fillMaxWidth()
                .padding(horizontal = paddingDefault))
        }
    }
}
