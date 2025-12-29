package wallapp.content.state.upgrade.plus.paywall

import androidx.compose.runtime.Immutable
import co.touchlab.skie.configuration.annotations.SealedInterop
import wallapp.content.state.error.ErrorViewState
import wallapp.content.state.upgrade.PaywallViewEventSink
import wallapp.pixel.image.ImageViewState
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.pager.LastPagerStateUpdateSink
import wallapp.pixel.screen.ScreenViewState
import wallapp.pixel.tab.TabsViewState
import wallapp.pixel.view.View
import wallapp.remotepaywall.RemotePaywallCallbacks
import wallapp.remotepaywall.RemotePaywallEvent
import wallapp.remotepaywall.RemotePaywallUiController
import wallapp.theme.Theme

@Immutable
@SealedInterop.Enabled
sealed class PaywallViewState : ScreenViewState {

    @Immutable
    data object Loading : PaywallViewState()

    @Immutable
    data class Native(
        val theme: Theme,
        val close: MenuItem,
        val featureImageViewState: ImageViewState,
        val subscriptionTitle: MenuItem,
        val paywallPlanView: View,
        val actionButton: MenuItem,
        val footerItem: MenuItem,
        val showAsModalSheet: Boolean,
    ) : PaywallViewState()

    @Immutable
    data class NativeTabs(
        val theme: Theme,
        val close: MenuItem,
        val featureImageViewState: ImageViewState,
        val subscriptionTitle: MenuItem,
        val initialTabPage: Int,
        val planSelectionTabs: TabsViewState,
        val planSelectionLastPagerStateUpdateSink: LastPagerStateUpdateSink?,
        val actionButton: MenuItem,
        val footerItem: MenuItem,
        val showAsModalSheet: Boolean,
        val paywallViewEventSink: PaywallViewEventSink,
    ) : PaywallViewState()

    /**
     *
     */
    @Immutable
    data class Remote(
        val remotePaywallUiController: RemotePaywallUiController,
        val remotePaywallEvent: RemotePaywallEvent,
        val remotePaywallCallbacks: RemotePaywallCallbacks,
        val fallback: Native,
    ) : PaywallViewState()

    @Immutable
    data class Error(
        val errorViewState: ErrorViewState,
    ) : PaywallViewState()

}
