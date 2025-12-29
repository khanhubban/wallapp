package wallapp.billing.remotepaywall

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import wallapp.pixel.render.Render
import wallapp.remotepaywall.RemotePaywallCallbacks
import wallapp.remotepaywall.RemotePaywallEvent
import wallapp.remotepaywall.RemotePaywallUiController

@Composable
actual fun PaywallContent(
    render: Render,
    remotePaywallUiController: RemotePaywallUiController,
    remotePaywallEvent: RemotePaywallEvent,
    modifier: Modifier,
    remotePaywallCallbacks: RemotePaywallCallbacks?,
    loadingComposable: @Composable (() -> Unit),
    errorComposable: @Composable ((Throwable) -> Unit),
) {

}